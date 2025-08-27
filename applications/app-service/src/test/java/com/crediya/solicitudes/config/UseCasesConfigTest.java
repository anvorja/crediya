// applications/app-service/src/test/java/com/crediya/solicitudes/config/UseCasesConfigTest.java
package com.crediya.solicitudes.config;

import com.crediya.solicitudes.model.solicitud.gateways.EventPublisherGateway;
import com.crediya.solicitudes.model.solicitud.gateways.NotificationGateway;
import com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import com.crediya.solicitudes.model.solicitud.gateways.ValidacionExternaGateway;
import com.crediya.solicitudes.usecase.crearsolicitud.CrearSolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            // Verificar que el ComponentScan detecta y registra el UseCase
            CrearSolicitudUseCase useCase = context.getBean(CrearSolicitudUseCase.class);
            assertNotNull(useCase, "CrearSolicitudUseCase debe estar registrado por ComponentScan");

            // Verificar que existe por nombre (ComponentScan usa convención camelCase)
            assertTrue(context.containsBean("crearSolicitudUseCase"),
                    "Bean 'crearSolicitudUseCase' debe existir");

            // Verificar que el bean está correctamente inicializado
            String[] beanNames = context.getBeanDefinitionNames();
            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.equals("crearSolicitudUseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }
            assertTrue(useCaseBeanFound, "ComponentScan debe haber detectado CrearSolicitudUseCase");
        }
    }

    /**
     * Configuración de test que simula el contexto real
     * con mocks de las dependencias necesarias
     */
    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        // Mock beans usando Mockito puro (sin @MockBean deprecated)
        @Bean
        public SolicitudRepository solicitudRepository() {
            return Mockito.mock(SolicitudRepository.class);
        }

        @Bean
        public NotificationGateway notificationGateway() {
            return Mockito.mock(NotificationGateway.class);
        }

        @Bean
        public EventPublisherGateway eventPublisherGateway() {
            return Mockito.mock(EventPublisherGateway.class);
        }

        @Bean
        public ValidacionExternaGateway validacionExternaGateway() {
            return Mockito.mock(ValidacionExternaGateway.class);
        }
    }
}