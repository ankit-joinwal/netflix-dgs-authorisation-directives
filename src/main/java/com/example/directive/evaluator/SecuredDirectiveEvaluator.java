package com.example.directive.evaluator;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * Evaluates a SpEL expression
 */
@Component
public class SecuredDirectiveEvaluator {


    private final BeanFactory beanFactory;

    public SecuredDirectiveEvaluator( BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public boolean evaluateExpression(String expressionValue,String userUuid, String fieldName) {

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

        context.setVariable("userUuid", userUuid);

        ExpressionParser expressionParser = new SpelExpressionParser();

        Expression expression = expressionParser.parseExpression(expressionValue);

        return expression.getValue(context, Boolean.class);

    }
}
