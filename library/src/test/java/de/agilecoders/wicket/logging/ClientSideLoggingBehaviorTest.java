package de.agilecoders.wicket.logging;

import de.agilecoders.wicket.logging.util.CollectionType;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the {@link ClientSideLoggingBehavior}
 *
 * @author miha
 */
public class ClientSideLoggingBehaviorTest {

    private WicketTester tester;

    @Before
    public void setUp() throws Exception {
        tester = new WicketTester(new ClientSideLoggingMockApplication());
    }

    @After
    public void tearDown() throws Exception {
        tester.destroy();
    }

    @Test
    public void builderConstructsCorrectBehavior() {
        ClientSideLoggingBehavior.Builder b = ClientSideLoggingBehavior.newBuilder();

        assertThat(b.build(), is(notNullValue()));
        assertThat(b.data().size(), is(equalTo(0)));
    }

    @Test
    public void builderConstructsCorrectBehaviorWhenUsingNonDefaultSettings() {
        ClientSideLoggingBehavior.Builder b = ClientSideLoggingBehavior.newBuilder()
                .collectClientInfos(false)
                .collectionType(CollectionType.Unload)
                .collectionTimer(Duration.seconds(100));

        assertThat(b.data().size(), is(equalTo(3)));
        assertThat(b.data().containsKey("collectClientInfos"), is(true));
        assertThat(b.data().containsKey("collectionType"), is(true));
        assertThat(b.data().containsKey("collectionTimer"), is(true));
    }

    @Test
    public void builderWithSpecConstructsCorrectBehaviorWhenUsingNonDefaultSettings() {
        ClientSideLoggingBehavior.Builder b = ClientSideLoggingBehavior.newBuilder("collectClientInfos=false,collectionType=Unload,collectionTimer=100 seconds");

        assertThat(b.data().size(), is(equalTo(3)));
        assertThat(b.data().containsKey("collectClientInfos"), is(true));
        assertThat(b.data().containsKey("collectionType"), is(true));
        assertThat(b.data().containsKey("collectionTimer"), is(true));
    }

    @Test
    public void builderConstructsCorrectSubclassOfBehavior() {
        ClientSideLoggingBehavior.Builder b = ClientSideLoggingBehavior.newBuilder();

        assertThat(b.build(MyBehavior.class), is(notNullValue()));
        assertThat(b.build(MyBehavior.class), is(instanceOf(MyBehavior.class)));
    }

    public static class MyBehavior extends ClientSideLoggingBehavior {
        public MyBehavior(Map<String, Object> data) {
            super(data);
        }
    }
}
