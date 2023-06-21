FROM eclipse-temurin:17.0.6_10-jdk as base

ENV SCALA_VERSION 2.13.11
ENV SBT_VERSION 1.8.3

# Install tools...
WORKDIR /opt/tools
# scala
RUN curl -fsL https://github.com/lampepfl/dotty/releases/download/${SCALA_VERSION}/scala3-${SCALA_VERSION}.tar.gz | tar xfz - -C .
# sbt
RUN curl -fsL https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz | tar xfz - -C .

# PATH
ENV PATH "${PATH}:/opt/tools/scala3-${SCALA_VERSION}/bin:/opt/tools/sbt/bin"

# Test
WORKDIR /tmp/test-tools
RUN javac -version
RUN java -version
RUN scalac -version
RUN scala -version
RUN sbt -v sbtVersion -Dsbt.rootdir=true
RUN chmod -R 777 /tmp

WORKDIR /root

FROM base as builder
COPY build.sbt .
COPY project/ ./project/
RUN sbt update
COPY src/ ./src/
RUN sbt clean coverageOn test coverageReport coverageOff
RUN sbt "set assembly/test := {}" assembly
RUN sbt dependencyUpdatesReport

FROM eclipse-temurin:17-jre-focal as release

COPY --from=builder /root/target/scala-2.13/burger-quizz.jar .

ENV TINI_VERSION v0.19.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

RUN groupadd -r myapp && useradd -r -g myapp myapp
USER myapp

ENTRYPOINT ["/tini", "--"]
CMD ["java", "-jar", "burger-quizz.jar"]
