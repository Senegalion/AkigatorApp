FROM node:20 as build
WORKDIR /app
COPY . .
RUN npm install
RUN chmod +x node_modules/.bin/react-scripts
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
