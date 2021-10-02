FROM clojure:openjdk-17-tools-deps-1.10.3.943

WORKDIR /app

ADD deps.edn .
RUN clojure -e "(prn :install)"

COPY src ./src

EXPOSE 6000

CMD ["clojure", "-m", "core"]