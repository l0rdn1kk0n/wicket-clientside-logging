package de.agilecoders.wicket.logging;

import com.google.common.collect.Lists;
import org.apache.wicket.mock.MockRequestParameters;
import org.apache.wicket.util.time.Time;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the default implementation of {@link IParamValueExtractor}
 *
 * @author miha
 */
public class IParamValueExtractorTest {

    @Test
    public void clientInfoWillBeExtracted() {
        IParamValueExtractor extractor = new IParamValueExtractor.DefaultParamValueExtractor();

        MockRequestParameters params = new MockRequestParameters();
        params.setParameterValue(ParamNames.AJAX_BASE_URL, "ajaxBaseUrl");
        params.setParameterValue(ParamNames.SCREEN_SIZE, "screenSize");
        params.setParameterValue(ParamNames.WINDOW_SIZE, "winSize");
        params.setParameterValue(ParamNames.USER_AGENT, "userAgent");

        IParamValueExtractor.Result result = extractor.parse(params);

        assertThat(result.clientInfos().ajaxBaseUrl(), is(equalTo("ajaxBaseUrl")));
        assertThat(result.clientInfos().screenSize(), is(equalTo("screenSize")));
        assertThat(result.clientInfos().windowSize(), is(equalTo("winSize")));
        assertThat(result.clientInfos().userAgent(), is(equalTo("userAgent")));
    }

    @Test
    public void logMessagesWillBeExtracted() {
        IParamValueExtractor extractor = new IParamValueExtractor.DefaultParamValueExtractor();

        MockRequestParameters params = new MockRequestParameters();
        addMessage(1, params, "error", "message 1", Time.valueOf(new Date(1982, 12, 14, 8, 0)).toRfc1123TimestampString(), "stacktrace 1");
        addMessage(2, params, "error", "message 2", Time.valueOf(new Date(1982, 12, 14, 12, 0)).toRfc1123TimestampString(), "stacktrace 2");
        addMessage(3, params, "error", "message 3", Time.valueOf(new Date(1982, 12, 14, 16, 0)).toRfc1123TimestampString(), "stacktrace 3");

        params.setParameterValue(ParamNames.LEVEL + "_" + 4, "warn");
        params.setParameterValue(ParamNames.MESSAGE + "_" + 5, "message 5");
        params.setParameterValue(ParamNames.TIMESTAMP + "_" + 6, "timestamp 6");

        IParamValueExtractor.Result result = extractor.parse(params);

        assertThat(Lists.newArrayList(result.logObjects()).size(), is(equalTo(3)));
        assertThat(result.logObjects(), hasItem(new ClientSideLogObject("error", "message 1", Time.valueOf(new Date(1982, 12, 14, 8, 0)).toRfc1123TimestampString(), "stacktrace 1", 1)));
        assertThat(result.logObjects(), hasItem(new ClientSideLogObject("error", "message 2", Time.valueOf(new Date(1982, 12, 14, 12, 0)).toRfc1123TimestampString(), "stacktrace 2", 2)));
        assertThat(result.logObjects(), hasItem(new ClientSideLogObject("error", "message 3", Time.valueOf(new Date(1982, 12, 14, 16, 0)).toRfc1123TimestampString(), "stacktrace 3", 3)));
    }

    private void addMessage(int index, MockRequestParameters params, String level, String message, String utcTimestamp, String stacktrace) {
        params.setParameterValue(ParamNames.LEVEL + "_" + index, level);
        params.setParameterValue(ParamNames.MESSAGE + "_" + index, message);
        params.setParameterValue(ParamNames.TIMESTAMP + "_" + index, utcTimestamp);
        params.setParameterValue(ParamNames.STACKTRACE + "_" + index, stacktrace);
    }

}
