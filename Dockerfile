# Multi-stage build para otimizar o tamanho da imagem
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar arquivos de dependências primeiro para aproveitar cache do Docker
COPY pom.xml .

# Baixar dependências primeiro (melhora o cache)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Imagem de produção - usando distroless para menor tamanho e segurança
FROM gcr.io/distroless/java17-debian11:nonroot

WORKDIR /app

# Copiar o JAR da aplicação
COPY --from=build /app/target/wallet-service-1.0.0.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"] 