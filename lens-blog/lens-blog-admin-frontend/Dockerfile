FROM node:8.17.0 as build-stage
WORKDIR /app
COPY package*.json ./
# RUN npm config set proxy http://10.158.100.6:8080
# RUN npm config set registry http://artifactory-espoo-fnms.int.net.nokia.com:8081/artifactory/api/npm/npm-virtual/
RUN npm install
COPY ./ .
RUN npm run build

FROM nginx as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
COPY nginx.conf /etc/nginx/nginx.conf
