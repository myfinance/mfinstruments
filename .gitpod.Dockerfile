FROM gitpod/workspace-full
# install java. for version upgrade see https://sdkman.io/usage
RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && sdk install java 17.0.1-tem"