FROM theasp/clojurescript-nodejs:latest AS builder

WORKDIR /app

COPY . .

RUN npm install && npm run build

FROM nginx:latest

COPY --from=builder /app/public /usr/share/nginx/html/

EXPOSE 80