/*  Copyright (c) 2006-2007, Vladimir Nikic
    All rights reserved.

    Redistribution and use of this software in source and binary forms,
    with or without modification, are permitted provided that the following
    conditions are met:

 * Redistributions of source code must retain the above
      copyright notice, this list of conditions and the
      following disclaimer.

 * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the
      following disclaimer in the documentation and/or other
      materials provided with the distribution.

 * The name of Web-Harvest may not be used to endorse or promote
      products derived from this software without specific prior
      written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.

    You can contact Vladimir Nikic by sending e-mail to
    nikic_vladimir@yahoo.com. Please include the word "Web-Harvest" in the
    subject line.
 */
package org.webharvest.runtime.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webharvest.annotation.Definition;
import org.webharvest.definition.HttpHeaderDef;
import org.webharvest.exception.HttpException;
import org.webharvest.runtime.DynamicScopeContext;
import org.webharvest.runtime.processors.plugins.Autoscanned;
import org.webharvest.runtime.processors.plugins.TargetNamespace;
import org.webharvest.runtime.templaters.BaseTemplater;
import org.webharvest.runtime.variables.EmptyVariable;
import org.webharvest.runtime.variables.Variable;

import static org.webharvest.WHConstants.XMLNS_CORE;
import static org.webharvest.WHConstants.XMLNS_CORE_10;

/**
 * Variable definition http header processor.
 */
// TODO Add unit test
// TODO Add javadoc
@Autoscanned
@TargetNamespace({XMLNS_CORE, XMLNS_CORE_10})
@Definition(value = "http-header", validAttributes = {"id", "name"},
        requiredAttributes = "name", definitionClass = HttpHeaderDef.class)
public class HttpHeaderProcessor extends AbstractProcessor<HttpHeaderDef>
{

    Logger LOGGER = LoggerFactory.getLogger(HttpHeaderProcessor.class);

    public Variable execute(DynamicScopeContext context)
            throws InterruptedException
    {
        String name = BaseTemplater.evaluateToString(elementDef.getName(),
                null, context);

        final HttpProcessor httpProcessor =
                (HttpProcessor) getParentProcessor();
        if (httpProcessor != null)
        {
            String headerValue = getBodyTextContent(elementDef, context).toString();
            if (headerValue.contains("\n") || headerValue.contains("\r"))
            {


                LOGGER.warn("Line {}:{}  Newline character found in the header {} value. Value is: {}. Replacing newlines with space.",
                        new Object[]{
                                elementDef.getLineNumber(),
                                elementDef.getColumnNumber(),
                                name,
                                headerValue
                        });
                headerValue = headerValue.replaceAll("\\r|\\n", " ");
            }
            httpProcessor.addHttpHeader(name, headerValue);
            this.setProperty("Name", name);
        } else
        {
            throw new HttpException(
                    "Usage of http-header processor is not allowed outside of http processor!");
        }

        return EmptyVariable.INSTANCE;
    }

}