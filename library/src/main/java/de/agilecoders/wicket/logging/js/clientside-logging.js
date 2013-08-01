/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * client side logic to catch errors and log them and for a simple
 * logger functionality.
 *
 *  - there are two public APIs: window.Log and $.Log (the name can be changed by "loggerName" option)
 *  - both contains a trace, debug, info, warn and error logger.
 *
 * @author miha
 */
;
(function ($, Wicket, amplify, win) {
    'use strict';

    if (typeof(WicketClientSideLogging) === 'object') {
        return;
    }

    /**
     * Base logger class that's responsible to send log messages.
     *
     * @type Object
     */
    win.WicketClientSideLogging = {

        LVL_OFF: "off",
        LVL_ERROR: "error",
        LVL_WARN: "warn",
        LVL_INFO: "info",
        LVL_DEBUG: "debug",
        LVL_TRACE: "trace",

        /**
         * logs an error message
         *
         * @param message the message to log
         */
        error: function (message) {
            this.log(this.LVL_ERROR, message, stacktrace());
        },

        /**
         * logs an error message without a stacktrace
         *
         * @param message the message to log
         */
        errorWithoutStack: function (message) {
            this.log(this.LVL_ERROR, message, "");
        },

        /**
         * logs a warn message
         *
         * @param message the message to log
         */
        warn: function (message) {
            this.log(this.LVL_WARN, message, "");
        },

        /**
         * logs an info message
         *
         * @param message the message to log
         */
        info: function (message) {
            this.log(this.LVL_INFO, message, "");
        },

        /**
         * logs a debug message
         *
         * @param message the message to log
         */
        debug: function (message) {
            this.log(this.LVL_DEBUG, message, "");
        },

        /**
         * logs a trace message
         *
         * @param message the message to log
         */
        trace: function (message) {
            this.log(this.LVL_TRACE, message, "");
        },

        /**
         * logs a message
         *
         * @param lvl the log level to use
         * @param message the message to log
         * @param stacktrace current stacktrace
         */
        log: function (lvl, message, stacktrace) {
            if (this.isLoggingActive(lvl)) {
                sendMessage({
                    lvl: lvl,
                    stack: stacktrace,
                    msg: message
                });
            }

            if (defaults.debug === true) {
                this.consoleLog(lvl, message, stacktrace);
            }
        },

        /**
         * logs a message to console if available
         *
         * @param lvl the log level to use
         * @param message the message to log
         * @param stacktrace current stacktrace
         */
        consoleLog: function (lvl, message, stacktrace) {
            if (win.console) {
                var msg = "[" + lvl + "] " + message;

                if (stacktrace && stacktrace != "") {
                    msg += "\n" + stacktrace;
                }

                if (win.console[lvl]) {
                    win.console[lvl(msg)];
                }
                else if (win.console.log) {
                    win.console.log(msg);
                }
            }
        },

        /**
         * checks whether logging is active for given log level
         * or not.
         *
         * @param lvl the log level to check
         * @returns {boolean} TRUE, if logging is active for given log level
         */
        isLoggingActive: function (lvl) {
            return logLevel > 0 && toInt(lvl) <= logLevel;
        }
    };

    var logLevel = 0;
    var queue = [];
    var noOfWinOnError = 0;
    var defaults = {
        replaceWicketLog: false,
        replaceWindowOnError: false,
        wrapWindowOnError: true,
        wrapWicketLog: true,
        flushMessagesOnUnload: true,
        logStacktrace: false,
        logAdditionalErrors: true,
        collectClientInfos: true,
        logLevel: win.WicketClientSideLogging.LVL_ERROR,
        url: null,
        method: 'POST',
        maxQueueSize: 10,
        loggerName: "Log",
        debug: false,
        collectionTimer: 5000,
        collectionType: "single"  // single, timer, size, unload, localstorage
    };

    /**
     * transforms a log level to its integer representation. The
     * integer representation of a log level is strictly ordered, which
     * means that trace has a higher value than error.
     *
     * @param lvl the log level to transform
     * @returns {number} integer representation of given log level
     */
    function toInt(lvl) {
        switch (lvl) {
            case win.WicketClientSideLogging.LVL_OFF:
                return 0;
            case win.WicketClientSideLogging.LVL_ERROR:
                return 1;
            case win.WicketClientSideLogging.LVL_WARN:
                return 2;
            case win.WicketClientSideLogging.LVL_INFO:
                return 3;
            case win.WicketClientSideLogging.LVL_DEBUG:
                return 4;
            case win.WicketClientSideLogging.LVL_TRACE:
                return 5;
        }

        return 0;
    }

    /**
     * sends given data to backend according to the current collection
     * type.
     *
     *  - single: each message will be sent to the backend directly
     *  - timer: after a configurable (defaults.collectionTimer) amount of time all queued messages will be sent to backend
     *  - size: after a configurable (defaults.maxQueueSize) size of queue all queued messages will be sent to backend
     *  - unload: messages will be queued and sent to the backend on page unload
     *  - localstorage: messages will be collected in localStorage and sent to the backend on next page load
     *
     * @param data the log data to send to backend
     */
    function sendMessage(data) {
        data.timestamp = (new Date()).toUTCString();

        if (defaults.collectionType === "single") {
            sendQueue([data], true);
        }
        else if (defaults.collectionType === "localstorage") {
            var currentValue = amplify.store("clientside-logging");

            if (!currentValue) {
                currentValue = [];
            }

            currentValue.push(data);
            amplify.store("clientside-logging", currentValue);
        }
        else if (defaults.collectionType === "timer" || defaults.collectionType === "unload") {
            queue.push(data);
        }
        else if (defaults.collectionType === "size") {
            queue.push(data);

            if (queue.length >= defaults.maxQueueSize) {
                flushMessages(true);
            }
        }
        else {
            throw new Error("invalid collection type: " + defaults.collectionType + "; must be one of: [single, timer, size, unload, localstorage]");
        }
    }

    /**
     * flushes all queued messages to the backend.
     *
     * @param async whether to send messages asynchronously or not
     */
    function flushMessages(async) {
        if (defaults.collectionType === "localstorage") {
            var currentValue = amplify.store("clientside-logging");

            if (currentValue && currentValue.length > 0) {
                sendQueue(currentValue, async);
            }
        }
        else {
            sendQueue(queue, async);
        }
    }

    /**
     * executes the ajax call
     *
     * @param q an array of log messages
     * @param async whether to send messages asynchronously or not
     */
    function sendQueue(q, async) {
        if (!q || q.length <= 0) {
            return;
        }

        // default mode is async
        async = async !== false;

        var data = appendClientInfo({}), i = 1;

        while (q.length > 0) {
            var e = q.pop();

            data["timestamp_" + i] = e.timestamp;
            data["msg_" + i] = e.msg;
            data["lvl_" + i] = e.lvl;

            if (defaults.logStacktrace && e.stack) {
                data["stack_" + i] = e.stack;
            }

            i++;
        }

        $.ajax({
            type: defaults.method,
            url: defaults.url,
            cache: false,
            async: async,
            data: data
        });
    }

    /**
     * @returns {string} current stacktrace
     */
    function stacktrace() {
        if (defaults.logStacktrace && win["printStackTrace"]) {
            return win.printStackTrace().join("\n");
        }
        else {
            return "";
        }
    }

    /**
     * appends special client information to given data object.
     *
     * @param data the data object to a some client information to
     * @returns {Object} the enhanced data object
     */
    function appendClientInfo(data) {
        data.ajaxBaseUrl = Wicket.Ajax.baseUrl || '.';

        if (defaults.collectClientInfos === true) {
            data.ua = navigator.userAgent;
            data.winSize = $(win).width() + 'x' + $(win).height();
            data.screenSize = win.screen.availWidth + 'x' + win.screen.availHeight;
        }
        return data;
    }

    /**
     * wraps/replaces the original Wicket.Log object
     *
     * @type Object
     */
    var WrappedWicketLog = {
        isManipulated: true,
        origWicketLog: null,

        override: function (origWicketLog) {
            this.origWicketLog = origWicketLog;

            return this;
        },

        enabled: function () {
            return this.origWicketLog.enabled();
        },

        info: function (msg) {
            win.WicketClientSideLogging.info(msg);

            if (defaults.wrapWicketLog === true && this.origWicketLog) {
                this.origWicketLog.info(msg);
            }
        },

        error: function (msg) {
            win.WicketClientSideLogging.error(msg);

            if (defaults.wrapWicketLog === true && this.origWicketLog) {
                this.origWicketLog.error(msg);
            }
        },

        log: function (msg) {
            win.WicketClientSideLogging.info(msg);

            if (defaults.wrapWicketLog === true && this.origWicketLog) {
                this.origWicketLog.log(msg);
            }
        }
    };

    /**
     * creates a wrapped window on error handler
     *
     * @param origWindowOnError the original window.onerror handler
     * @returns {Function} wrapped window.onerror handler
     */
    function wrappedWindowOnError(origWindowOnError) {
        return function (message, file, line) {
            noOfWinOnError++;

            if (noOfWinOnError == 1 || defaults.logAdditionalErrors) {
                var log = message + " on [" + file + ":" + line + "]";

                win.WicketClientSideLogging.errorWithoutStack(log);
            }

            if (defaults.wrapWindowOnError === true && origWindowOnError) {
                try {
                    origWindowOnError.call(this, message, file, line);
                }
                catch (e) {
                    /*ignore*/
                }
            }
        }
    }

    /**
     * jquery plugin definition
     *
     * @param options these options will override the default options
     */
    $.wicketClientSideLogging = function (options) {
        defaults = $.extend(defaults, options || {});

        if (!defaults.url || defaults.url.length == 0) {
            throw new Error("there's no valid url set: " + defaults.url);
        }

        logLevel = toInt(defaults.logLevel);

        if (defaults.wrapWindowOnError === true || defaults.replaceWindowOnError === true) {
            win.onerror = wrappedWindowOnError(win.onerror)
        }

        if ((defaults.wrapWicketLog === true || defaults.replaceWicketLog === true) && (!Wicket.Log || Wicket.Log.isManipulated !== true)) {
            Wicket.Log = WrappedWicketLog.override(Wicket.Log)
        }

        if (defaults.collectionType === "timer") {
            win.setInterval(function () {
                flushMessages(true);
            }, defaults.collectionTimer);
        }

        if (defaults.collectionType === "localstorage") {
            $(win).load(function () {
                win.setTimeout(function () {
                    flushMessages(true);
                }, 500);
            });

            defaults.flushMessagesOnUnload = false;
        }

        if (defaults.flushMessagesOnUnload === true || defaults.collectionType === "unload") {
            $(win).on('beforeunload', function () {
                flushMessages(false);
            });
        }

        if (defaults.loggerName) {
            win[defaults.loggerName] = win.WicketClientSideLogging;
            $[defaults.loggerName] = win.WicketClientSideLogging;
        }
    };

}(jQuery, Wicket, amplify, window));
