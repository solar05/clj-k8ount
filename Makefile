checks: lint check-kibit check-eastwood kondo
lint:
	clojure -M:cljfmt-check
fix:
	clojure -M:cljfmt-fix
check-kibit:
	clojure -M:kibit
check-eastwood:
	clojure -M:eastwood
kondo:
	clj-kondo --lint src --config .clj-kondo/config.edn
test:
	clojure -M:test
.PHONY: test
