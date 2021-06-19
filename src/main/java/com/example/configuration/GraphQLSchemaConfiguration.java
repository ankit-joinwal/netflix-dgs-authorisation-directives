package com.example.configuration;

import com.example.directive.evaluator.SecuredDirectiveWiring;
import com.example.scalar.CustomScalars;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsRuntimeWiring;
import graphql.schema.idl.RuntimeWiring;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GraphQLSchemaConfiguration {
    @DgsComponent
    public class SecuredDirectiveRegistration {

        private SecuredDirectiveWiring securedDirectiveWiring;

        public SecuredDirectiveRegistration(SecuredDirectiveWiring securedDirectiveWiring) {
            this.securedDirectiveWiring = securedDirectiveWiring;
        }

        /**
         * Registers schema directive wiring for <code>@secured</code> directive.
         *
         * @param builder
         * @return RuntimeWiring.Builder
         */
        @DgsRuntimeWiring
        public RuntimeWiring.Builder addSecuredDirective(RuntimeWiring.Builder builder) {
            return builder.directive(SecuredDirectiveWiring.SECURED_DIRECTIVE, securedDirectiveWiring);
        }

    }

    @DgsComponent
    public class ScalarsRegistration {
        @DgsRuntimeWiring
        public RuntimeWiring.Builder addCustomScalars(RuntimeWiring.Builder builder) {
            return builder.scalar(CustomScalars.UUID);

        }
    }


}
