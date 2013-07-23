# WICKET CLIENTSIDE LOGGING

wicket-clientside-logging is a helper library that allows javascript logging on client side, all log messages will be stored on server side too.

Current build status: [![Build Status](https://buildhive.cloudbees.com/job/l0rdn1kk0n/job/wicket-clientside-logging/badge/icon)](https://buildhive.cloudbees.com/job/l0rdn1kk0n/job/wicket-clientside-logging/) [![Build Status](https://travis-ci.org/l0rdn1kk0n/wicket-clientside-logging.png?branch=master)](https://travis-ci.org/l0rdn1kk0n/wicket-clientside-logging)

## Dependencies

* Apache Wicket (6.9.1): http://wicket.apache.org/

## Installation

### Maven

wicket-clientside-logging is [available](http://search.maven.org/#artifactdetails|de.agilecoders.wicket|wicket-clientside-logging|0.1.0|jar) in Maven central repository.

core maven dependency:
<pre><code>&lt;dependency&gt;
  &lt;groupId&gt;de.agilecoders.wicket&lt;/groupId&gt;
  &lt;artifactId&gt;wicket-clientside-logging&lt;/artifactId&gt;
  &lt;version&gt;0.1.0&lt;/version&gt;
&lt;/dependency&gt;
</code></pre>

### Backend

<pre><code>// Install settings class; best place to do this is in Application#init()
ClientSideErrorLoggingSettings.install(Application.get());
</code></pre>

<pre><code>// add the ClientSideErrorLoggingBehavior to your page
public class MyPage extends Page {
  public MyPage(PageParameters params) {
    super(params);

    add(new ClientSideErrorLoggingBehavior());
  }
}
</code></pre>

### Frontend

<pre><code>// use Log object or Wicket.Log on client side
function myFunc() {
  Log.error("this will be sent to server.");
  Wicket.Log.error("this too.");
  $.Log.error("also this is sent to server...");
}
</code></pre>

Supported log methods: `error`, `warn`, `info`, `debug` and `trace`
A log method won't do anything as long as you don't use a log level that contains the level of the method. The default log level is set to `error` which means that methods like `warn`, `info`, `debug` and `trace` won't send data to the server.

## Configuration

There are some default arguments that you can override.

Configuration of ClientSideErrorLoggingSettings:

	new ClientSideErrorLoggingSettings()
		.level(level)					// sets the log level for client side logger (default: error)
		.debug(bool)					// whether to activate debug mode or not; in debug mode all log messages will be written to console.log too (default: false)
		.logger(logger)					// defines the logger that is used on server side (default: slf4j)
		.cleaner(cleaner)				// a cleaner is responsible for cleaning log messages; (default: remove all [\r\n\t])

Configuration of ClientSideErrorLoggingBehavior:

	ClientSideErrorLoggingBehavior.newBuilder()
		.replaceWicketLog()				// whether to replace Wicket.Log or not (default: false)
		.replaceWindowOnError()			// whether to replace window.onerror or not (default: false)
		.wrapWicketLog()				// whether to wrap Wicket.Log or not, all calls to Wicket.Log will be sent to server and to original Wicket.Log object (default: true)
		.wrapWindowOnError()			// whether to wrap window.onerror or not, all window.onerror events will be sent to server and to original window.onerror handler (default: true)
		.flushMessagesOnUnload()		// If set to true all log messages will be sent synchronously to server when a page unload event is fired (default: true)
		.collectionTimer(duration)		// Sets the interval between two server calls, all messages between will be queued, this is only used if collectionType is set to "timer" (default: 5000)
		.maxQueueSize(size)				// Sets the maximum queue size, if max size is exceeded all messages will be sent to server (default: 10)
		.collectionType(type)			// Sets the collection type (default: single, other: timer, size)
		.loggerName(name)				// Sets the logger name that is used on client side, e.g. name="Log": Log.info('message'); (default: Log)
	.build();

### QA

#### How to change log format on server side?

<pre><code>public class MyApplication extends WebApplication {
  protected void init() {
    ClientSideErrorLoggingSettings settings = new ClientSideErrorLoggingSettings();
  
    settings.logger(new IClientLogger.DefaultClientLogger(settings.id()) {
        protected String newLogMessage(ClientSideLogObject logObject, ClientInfos clientInfos, ILogCleaner cleaner) {
            return String.format("[%s] %s; UserAgent: %s; WindowSize: %s", 
                cleaner.toCleanPath(clientInfos.ajaxBaseUrl()), 
                logObject, 
                cleaner.clean(clientInfos.userAgent()),
                cleaner.clean(clientInfos.windowSize()));
        }  
    });

    ClientSideErrorLoggingSettings.install(this, settings);
  }
}
</code></pre>

## Bug tracker

Have a bug? Please create an issue here on GitHub!

https://github.com/l0rdn1kk0n/wicket-clientside-logging/issues

## How to get help and news

* Keep up to date on announcements and more by following me [@l0rdn1kk0n](http://twitter.com/l0rdn1kk0n) on Twitter
* how to use Wicket Clientside Logging? Read the documentation on https://github.com/l0rdn1kk0n/wicket-clientside-logging/wiki.
* read more on my [blog](http://blog.agilecoders.de/).

## Versioning

Wicket-Clientside-Logging will be maintained under the Semantic Versioning guidelines as much as possible.

Releases will be numbered with the follow format:

`<major>.<minor>.<patch>`

And constructed with the following guidelines:

* Breaking backward compatibility bumps the major
* New additions without breaking backward compatibility bumps the minor
* Bug fixes and misc changes bump the patch

For more information on SemVer, please visit http://semver.org/.


## Copyright and license

Copyright 2012 AgileCoders.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
