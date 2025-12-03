FROM node:22-alpine

WORKDIR /app

COPY package*.json ./

RUN npm ci --legacy-peer-deps

COPY . .

# Vite läuft im Container auf 3000
EXPOSE 3000

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]