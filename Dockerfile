FROM opensearchstaging/opensearch:2.18.0

ARG VERSION="1.0.0-SNAPSHOT"

COPY ./build/distributions/phinder-${VERSION}.zip /tmp/

RUN /usr/share/opensearch/bin/opensearch-plugin install --batch file:/tmp/phinder-${VERSION}.zip