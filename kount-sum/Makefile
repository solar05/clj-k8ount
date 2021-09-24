run:
	clojure -m core
checks: lint check-kibit kondo
lint:
	clojure -M:cljfmt-check
fix:
	clojure -M:cljfmt-fix
check-kibit:
	clojure -M:kibit
kondo:
	clj-kondo --lint src --config .clj-kondo/config.edn
test:
	clojure -M:test
dbuild:
	docker-compose build
drun:
	docker-compose up
.PHONY: test
