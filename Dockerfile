FROM mcr.microsoft.com/devcontainers/universal:2

# Install Java 21 (LTS)
RUN apt-get update && apt-get install -y \
    curl \
    rlwrap \
    && rm -rf /var/lib/apt/lists/*

RUN curl -s "https://get.sdkman.io" | bash \
    && bash -c "source /root/.sdkman/bin/sdkman-init.sh \
    && sdk install java 21.0.3-tem \
    && sdk default java 21.0.3-tem"

ENV JAVA_HOME=/root/.sdkman/candidates/java/current
ENV PATH=$JAVA_HOME/bin:$PATH

# Install Clojure CLI
RUN curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh \
    && chmod +x linux-install.sh \
    && ./linux-install.sh \
    && rm linux-install.sh

# Install Node.js 20 LTS
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs

# Install shadow-cljs globally
RUN npm install -g shadow-cljs

# Pre-warm shadow-cljs dependencies (eliminates first-run download wait)
WORKDIR /tmp/warmup
COPY shadow-cljs.edn .
RUN shadow-cljs classpath || true

WORKDIR /workspaces
