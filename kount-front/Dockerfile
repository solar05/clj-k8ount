FROM theasp/clojurescript-nodejs:latest AS builder

WORKDIR /app

COPY . .

RUN npm install

RUN npm run build

FROM nginx:latest

COPY --from=builder /app/public /usr/share/nginx/html

copy conf/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80