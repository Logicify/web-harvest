package org.webharvest.runtime.processors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.unitils.mock.Mock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.webharvest.UnitilsTestNGExtension;
import org.webharvest.definition.XmlNodeTestUtils;
import org.webharvest.runtime.DynamicScopeContext;
import org.webharvest.utils.AlwaysTrueArgumentMatcher;

import java.util.concurrent.Callable;

/**
 * Tests newly implemented behavior of the HttpHeaderProcessor.
 */
public class HttpHeaderProcessorTest extends UnitilsTestNGExtension
{
    Mock<HttpProcessor> httpProcessorMock;
    Mock<DynamicScopeContext> contextMock;

    @BeforeMethod
    public void before() throws InterruptedException
    {
        contextMock.performs(new MockBehavior()
        {
            @Override
            public Object execute(ProxyInvocation proxyInvocation) throws Throwable
            {
                Callable c = (Callable) proxyInvocation.getArguments().get(0);
                return c.call();
            }
        }).executeWithinNewContext(AlwaysTrueArgumentMatcher.<Callable<Object>>always());
    }

    @Test
    public void testExecute_ShouldStripNewlinesFromHeaderValues() throws InterruptedException
    {
        // given
        HttpProcessor httpProcessor = httpProcessorMock.getMock();


        final Processor processor =
                ProcessorTestUtils.processor(
                        XmlNodeTestUtils.createXmlNode("" +
                                "<http-header name=\"User-Agent\">Mozilla\nSafari</http-header>",
                                XmlNodeTestUtils.NAMESPACE_10));
        processor.setParentProcessor(httpProcessor);

        // when
        processor.run(contextMock.getMock());

        // then
        httpProcessorMock.assertInvoked().addHttpHeader("User-Agent", "Mozilla Safari");
    }

}
