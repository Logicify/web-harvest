package org.webharvest.utils;


import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.argumentmatcher.ArgumentMatcherRepository;
import org.unitils.mock.core.proxy.StackTraceUtils;

/**
 * Very simple thing - matches ANY argument.
 * <p/>
 * Unfortunately, the {@link org.unitils.mock.argumentmatcher.impl.AnyArgumentMatcher} checks equality calling
 * x.getClass().equals(storedType), which means that anonymous implementations (callbacks, for instance) are not supported.
 */
public class AlwaysTrueArgumentMatcher implements ArgumentMatcher
{
    @org.unitils.mock.annotation.ArgumentMatcher
    public static <T> T always()
    {
        ArgumentMatcherRepository.getInstance().registerArgumentMatcher(new AlwaysTrueArgumentMatcher(),
                StackTraceUtils.getInvocationLineNr(AlwaysTrueArgumentMatcher.class));
        return null;
    }

    @Override
    public MatchResult matches(Object argument, Object argumentAtInvocationTime)
    {
        return MatchResult.MATCH;
    }
}
