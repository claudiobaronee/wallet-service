# Multi-stage build para otimizar o tamanho da imagem
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar arquivos de dependências primeiro para aproveitar cache do Docker
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Imagem de produção
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -S wallet && adduser -S -G wallet wallet

# Copiar o JAR da aplicação
COPY --from=build /app/target/wallet-service-1.0.0.jar app.jar

# Mudar propriedade do arquivo para o usuário wallet
RUN chown wallet:wallet app.jar

# Mudar para usuário não-root
USER wallet

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"] 