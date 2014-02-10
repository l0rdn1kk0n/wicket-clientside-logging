/*jslint browser: true, devel: false, forin: true, plusplus: true, todo: true, vars: true, white: true */
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
(function (win) {
    'use strict';

    if (typeof win.WicketClientSideLogging === 'object') {
        return;
    }

    var $, Wicket, amplify, sentEntriesOnPage = 0;

    // cache window onerror calls before this plugin is initialized
    var cachedErrors = [];
    var cachedOrigWinOnError = win.onerror;
    win.onerror = function (message, file, line) {
        cachedErrors.push({
            message: message,
            file: file,
            line: line,
            timestamp: currentTimestamp()
        });
    };

    var logLevelNames = ["off", "error", "warn", "info", "debug", "trace"];
    var logLevels = {
        /* will be generated from logLevelNames, i.e. something like
         LVL_OFF: 0,
         LVL_ERROR: 1,
         LVL_WARN: 2,
         LVL_INFO: 3,
         LVL_DEBUG: 4,
         LVL_TRACE: 5
         */
    };

    /**
     * Base logger class that's responsible to send log messages.
     *
     * @type Object
     */
    var WicketClientSideLogging = {

        /**
         * logs an error message
         *
         * @param message the message to log
         */
        error: function (message) {
            this.log(logLevels.LVL_ERROR, message, stacktrace());
        },

        /**
         * logs an error message without a stacktrace
         *
         * @param message the message to log
         */
        errorWithoutStack: function (message) {
            this.log(logLevels.LVL_ERROR, message, "");
        },

        /**
         * logs a warn message
         *
         * @param message the message to log
         */
        warn: function (message) {
            this.log(logLevels.LVL_WARN, message, "");
        },

        /**
         * logs an info message
         *
         * @param message the message to log
         */
        info: function (message) {
            this.log(logLevels.LVL_INFO, message, "");
        },

        /**
         * logs a debug message
         *
         * @param message the message to log
         */
        debug: function (message) {
            this.log(logLevels.LVL_DEBUG, message, "");
        },

        /**
         * logs a trace message
         *
         * @param message the message to log
         */
        trace: function (message) {
            this.log(logLevels.LVL_TRACE, message, "");
        },

        /**
         * logs a message
         *
         * @param {number} lvl the log level to use
         * @param message the message to log
         * @param stacktrace current stacktrace
         */
        log: function (lvl, message, stacktrace) {
            if (this.isLoggingActive(lvl)) {
                var logLevelName = logLevelNames[lvl];
                var log = {
                    lvl: logLevelName,
                    stack: stacktrace
                };

                if (typeof message === "string") {
                    log.msg = message;
                }
                else if (typeof message === "object") {
                    log.msg = message.message;
                    log.file = message.file;
                    log.line = message.line;
                }
                else {
                    log.message = message + "";
                }

                sendMessage(log);
            }

            if (defaults.debug === true) {
                this.consoleLog(lvl, message, stacktrace);
            }
        },

        /**
         * logs a message to console if available
         *
         * @param {number} lvl the log level to use
         * @param message the message to log
         * @param stacktrace current stacktrace
         */
        consoleLog: function (lvl, message, stacktrace) {
            var levelName = logLevelNames[lvl];

            if (win.console) {
                var msg = "[" + levelName + "] " + this.messageToString(message);

                if (stacktrace) {
                    msg += "\n" + stacktrace;
                }

                if (win.console[levelName]) {
                    win.console[levelName](msg);
                }
                else if (win.console.log) {
                    win.console.log(msg);
                }
            }
        },

        /**
         * transforms given message into a string
         *
         * @param {string|object} message
         * @returns {string}
         */
        messageToString: function (message) {
            if (typeof message === "object") {
                return message.message + " on [" + message.file + ":" + message.line + "]"
            }
            else {
                return message + "";
            }
        },

        /**
         * checks whether logging is active for given log level
         * or not and checks the max number of log entries per page.
         *
         * @param {number} lvl the log level to check
         * @returns {boolean} TRUE, if logging is active for given log level
         */
        isLoggingActive: function (lvl) {
            return defaults.logLevel > 0 && defaults.logLevel >= lvl &&
                   defaults.maxEntriesPerPage > (sentEntriesOnPage + queue.length);
        }
    };

    /**
     * There are several ways of how to send the messages to the backend.
     *
     * These methods define how a single message (given via a data object) is handled.
     *
     * @Type Object
     */
    var collectionTypes = {
        /*
         * Each message will be sent to the backend directly
         *
         * @param {Object} data The log data to send to backend
         */
        "single": function (data) {
            sendQueue([data], true);
        },

        /*
         * After a configurable (defaults.collectionTimer) amount of time all queued messages will be sent to backend
         *
         * @param {Object} data The log data to send to backend
         */
        "timer": function (data) {
            queue.push(data);
        },

        /*
         * After a configurable (defaults.maxQueueSize) size of queue all queued messages will be sent to backend
         *
         * @param {Object} data The log data to send to backend
         */
        "size": function (data) {
            queue.push(data);

            if (queue.length >= defaults.maxQueueSize) {
                flushMessages(true);
            }
        },

        /*
         * Messages will be queued and sent to the backend on page unload
         *
         * @param {Object} data The log data to send to backend
         */
        "unload": function (data) {
            queue.push(data);
        },

        /*
         * Messages will be collected in localStorage and sent to the backend on next page load
         */
        "localstorage": function (data) {
            var currentValue = amplify.store("clientside-logging");

            if (!currentValue) {
                currentValue = [];
            }

            currentValue.push(data);
            amplify.store("clientside-logging", currentValue);
        }
    };

    var queue = [], noOfWinOnError = 0;
    var defaults = {
        replaceWicketLog: false,
        replaceWindowOnError: false,
        wrapWindowOnError: true,
        wrapWicketLog: true,
        flushMessagesOnUnload: true,
        logStacktrace: false,
        logAdditionalErrors: true,
        dateFormat: null,
        collectClientInfos: true,
        logLevel: WicketClientSideLogging.LVL_ERROR,
        url: null,
        method: 'POST',
        maxQueueSize: 10,
        maxEntriesPerPage: 50,
        loggerName: "Log",
        customFilter: null,
        debug: false,
        collectionTimer: 5000,
        collectionType: "single"  // single, timer, size, unload, localstorage
    };

    /**
     * Returns an error message for an invalid collection type.
     */
    function getInvalidCollectionTypeMessage(type) {
        var keys = [], key;

        for (key in collectionTypes) {
            if (collectionTypes.hasOwnProperty(key)) {
                keys.push(key);
            }
        }

        return "Invalid collection type: " + type + "; must be one of: [" + keys.join(", ") + "]";
    }

    /**
     * @returns {number} current timestamp as integer since 1970
     */
    function currentTimestamp() {
        return defaults.dateFormat ? moment().format(defaults.dateFormat) : +(new Date());
    }

    /**
     * sends given data to backend according to the current collection
     * type.
     *
     * For information on the available collection types turn to the documentation
     * on the collectionTypes object.
     *
     * @param data the log data to send to backend
     */
    function sendMessage(data) {
        var type = defaults.collectionType;

        data.timestamp = currentTimestamp();

        if (collectionTypes.hasOwnProperty(type)) {
            collectionTypes[type](data);
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

                // clear queue, i.e. remove from localStorage
                amplify.store("clientside-logging", null);
            }
        }
        else {
            sendQueue(queue, async);
        }
    }

    /**
     * Processes the queue to a flat object that contains all data that should
     * be sent to the backend.
     *
     * @param {Array} queue
     * @return {Object}
     */
    function prepareData(queue) {
        var data = appendClientInfo({}), i = 1;

        while (queue.length > 0) {
            // removes the item from the queue
            var e = queue.pop();

            if (filter(e)) {
                data["timestamp_" + i] = e.timestamp;
                data["msg_" + i] = e.msg;
                data["lvl_" + i] = e.lvl;

                if (e.line) {
                    data["line_" + i] = e.line;
                }
                if (e.file) {
                    data["file_" + i] = e.file;
                }

                if (defaults.logStacktrace && e.stack) {
                    data["stack_" + i] = e.stack;
                }

                i++;
            }
        }

        return data;
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

        var data = prepareData(q);
        if (!data || data.length <= 0) {
            return;
        }

        sentEntriesOnPage = sentEntriesOnPage + data.length;

        // default mode is async
        async = async !== false;

        $.ajax({
            type: defaults.method,
            url: defaults.url,
            cache: false,
            async: async,
            dataType: "text",
            data: data
        });
    }

    /**
     * filters out an event object
     *
     * @param event {Object} the event to filter
     * @returns {boolean} whether to accept event or not (TRUE=send event to server, FALSE=skip)
     */
    function filter(event) {
        return event != null && typeof defaults.customFilter === "function" && defaults.customFilter(event);
    }

    /**
     * @returns {string} current stacktrace
     */
    function stacktrace() {
        if (defaults.logStacktrace && win.printStackTrace) {
            return win.printStackTrace().join("\n");
        }

        return "";
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
            WicketClientSideLogging.info(msg);

            if (defaults.wrapWicketLog === true && this.origWicketLog) {
                this.origWicketLog.info(msg);
            }
        },

        error: function (msg) {
            WicketClientSideLogging.error(msg);

            if (defaults.wrapWicketLog === true && this.origWicketLog) {
                this.origWicketLog.error(msg);
            }
        },

        log: function (msg) {
            WicketClientSideLogging.info(msg);

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

            if (noOfWinOnError === 1 || defaults.logAdditionalErrors) {
                WicketClientSideLogging.errorWithoutStack({
                    message: message,
                    file: file,
                    line: line
                });
            }

            if (defaults.wrapWindowOnError === true && origWindowOnError) {
                try {
                    origWindowOnError.call(this, message, file, line);
                }
                catch (e) {
                    /*ignore*/
                }
            }
        };
    }

    /**
     * Converts the given log level to a number. Defaults to logLevels.LVL_ERROR.
     *
     * @param {string|number} level Some representation of a level, either a number or a string like "error"
     *                              or "warn" or anything else.
     * @return {number}
     */
    function getLogLevelAsNumber(level) {
        // convert "error", "warn" etc.
        if (typeof level === "string") {
            var key = "LVL_" + level.toUpperCase();

            if (logLevels.hasOwnProperty(key)) {
                level = logLevels[key];
            }
        }

        // convert any other non-number value to logLevels.LVL_ERROR
        if (typeof level !== "number") {
            level = logLevels.LVL_ERROR;
        }

        // limit level to a reasonable value, i.e. a non-negative integer 
        return Math.max(0, Math.floor(level));
    }

    /**
     * transforms a function name into the function instance
     *
     * @param {String} functionName the function name to transform into its instance
     * @returns {Function} the function instance
     */
    function toFunction(functionName) {
        if (functionName.indexOf(".") > -1) {
            var parts = functionName.split(".");
            for (var i = 0, len = parts.length, obj = window; i < len; ++i) {
                if (obj[parts[i]]) {
                    obj = obj[parts[i]];
                } else {
                    return null;
                }
            }
            return typeof obj === "function" ? obj : null;
        } else if (typeof window[functionName] === "function") {
            return window[functionName];
        }
        return null;
    }

    /**
     * Initializes the logging.
     *
     * @param {Object} jQuery instance
     * @param {Object} W the wicket object
     * @param {Object} amp amplify object
     * @param {Object} options these options will override the default options
     */
    function initializeLogging(jQuery, W, amp, options) {
        $ = jQuery;
        Wicket = W;
        amplify = amp;

        // automatically generates the logLevel constants as denoted above
        $.each(logLevelNames, function (i, name) {
            logLevels["LVL_" + name.toUpperCase()] = i;
        });

        // merge log levels to WicketClientSideLogging so they are available
        // via WicketClientSideLogging.LVL_ERROR etc.
        $.extend(WicketClientSideLogging, logLevels);

        $.extend(defaults, options || {});

        if (typeof defaults.customFilter === "function") {
            // everything ok.
        } else if(typeof defaults.customFilter === "string" && defaults.customFilter.indexOf("return") == 0) {
            defaults.customFilter = new Function("event", defaults.customFilter);
        } else if(typeof defaults.customFilter === "string") {
            defaults.customFilter = toFunction(defaults.customFilter);
        } else {
            // invalid custom filter, remove it.
            defaults.customFilter = null;
        }

        if (!defaults.url) {
            throw new Error("there's no valid url set: " + defaults.url);
        }

        if (!collectionTypes.hasOwnProperty(defaults.collectionType)) {
            throw new Error(getInvalidCollectionTypeMessage(defaults.collectionType));
        }

        defaults.logLevel = getLogLevelAsNumber(defaults.logLevel);

        win.onerror = cachedOrigWinOnError;

        if (defaults.wrapWindowOnError === true || defaults.replaceWindowOnError === true) {
            win.onerror = wrappedWindowOnError(win.onerror);

            while (cachedErrors && cachedErrors.length > 0) {
                // removes the item from the queue
                var e = cachedErrors.pop();

                win.onerror(e.message, e.file, e.line);
            }
        }
        else {
            cachedErrors = [];
        }

        if ((defaults.wrapWicketLog === true || defaults.replaceWicketLog === true) && (!Wicket.Log || Wicket.Log.isManipulated !== true)) {
            Wicket.Log = WrappedWicketLog.override(Wicket.Log);
        }

        if (defaults.collectionType === "timer") {
            win.setInterval(function () {
                flushMessages(true);
            }, defaults.collectionTimer);
        }

        if (defaults.collectionType === "localstorage") {
            if (!amplify) {
                throw new Error("can't use collection type 'localeStorage' without amplify.");
            }

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
            win[defaults.loggerName] = WicketClientSideLogging;
            $[defaults.loggerName] = WicketClientSideLogging;
        }
    }

    // allow configuration via WicketClientSideLogging
    WicketClientSideLogging.initialize = initializeLogging;

    // make WicketClientSideLogging public via window
    win.WicketClientSideLogging = WicketClientSideLogging;

    // make WicketClientSideLogging configurable
    win.wicketClientSideLogging = initializeLogging;

    /**
     * Copyright (c) Mozilla Foundation http://www.mozilla.org/
     * This code is available under the terms of the MIT License
     *
     * if there's no filter method on Array: add it
     */
    function addFilterPrototype() {
        if (!Array.prototype.filter) {
            Array.prototype.filter = function (fun /*, thisp*/) {
                var len = this.length >>> 0;
                if (typeof fun != "function") {
                    throw new TypeError();
                }

                var res = [];
                var thisp = arguments[1];
                for (var i = 0; i < len; i++) {
                    if (i in this) {
                        var val = this[i]; // in case fun mutates this
                        if (fun.call(thisp, val, i, this)) {
                            res.push(val);
                        }
                    }
                }

                return res;
            };
        }
    }
}(window));
