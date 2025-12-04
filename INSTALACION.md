# 🥊 MMA LIVE - Sistema de Gestión de Peleas

## 📚 Proyecto Académico
- **Estudiante:** Mateo Mikael Pillajo Guijarro
- **Universidad:** Universidad de las Américas
- **Materia:** Ingeniería Web

---

## 🎯 Características del Sistema

✅ **Autenticación JWT** - Admin, Jueces, Supervisores  
✅ **Gestión de Peleadores** - CRUD completo con records  
✅ **Gestión de Peleas (Bouts)** - Estados, cronómetro, control  
✅ **Sistema de Puntuación** - 10-point must system  
✅ **WebSocket** - Actualizaciones en tiempo real  
✅ **Torneos** - Brackets y fases  
✅ **Eventos y Reglas** - Configuración personalizable  

---

## 📋 Requisitos

### Java 17+
```powershell
java -version
```

### Maven 3.9+
```powershell
mvn -version
```

---

## 🚀 Ejecución Local

### Opción 1: Script (Windows)
```
Doble clic en: iniciar-backend.bat
```

### Opción 2: Comando
```powershell
cd mma-demo-live
.\mvnw.cmd spring-boot:run
```

### Acceso
| Página | URL |
|--------|-----|
| 🏠 Inicio | http://localhost:8081 |
| 🔐 Login | http://localhost:8081/login.html |
| 📊 Dashboard | http://localhost:8081/ui/dashboard.html |

---

## 🔑 Credenciales de Demo

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `admin` | `admin123` | Administrador |
| `juez1` | `juez123` | Juez |
| `juez2` | `juez123` | Juez |
| `juez3` | `juez123` | Juez |
| `supervisor` | `super123` | Supervisor |

---

## 🌐 Deployment en la Nube

### Opción A: Railway (Recomendado - Gratis)

1. Crear cuenta en https://railway.app
2. Conectar repositorio de GitHub
3. Railway detecta automáticamente Spring Boot
4. Variables de entorno necesarias:
   ```
   PORT=8080
   JWT_SECRET=TuClaveSecretaAqui
   ```
5. Deploy automático

### Opción B: Render (Gratis)

1. Crear cuenta en https://render.com
2. New → Web Service
3. Conectar repositorio GitHub
4. Configurar:
   - **Runtime:** Docker
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/mma-demo-live-0.0.1-SNAPSHOT.jar`
5. Variables de entorno:
   ```
   PORT=8080
   JWT_SECRET=TuClaveSecretaAqui
   ```

### Opción C: Heroku

1. Instalar Heroku CLI
2. Comandos:
```bash
heroku login
heroku create mma-live-tuusuario
git push heroku main
```

---

## 📡 API REST

### Públicas (sin auth)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/public/fighters` | Listar peleadores |
| GET | `/api/public/bouts` | Listar peleas |
| GET | `/api/public/bouts/live` | Peleas en vivo |
| GET | `/api/public/stats` | Estadísticas |

### Admin (requiere JWT)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/admin/fighters` | Crear peleador |
| POST | `/api/admin/bouts` | Crear pelea |
| POST | `/api/admin/users` | Crear usuario |
| DELETE | `/api/admin/bouts/{id}` | Eliminar pelea |

### Juez (requiere JWT)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/judge/bouts/{id}/start` | Iniciar pelea |
| POST | `/api/judge/bouts/{id}/pause` | Pausar pelea |
| POST | `/api/judge/bouts/{id}/next-round` | Siguiente round |
| POST | `/api/judge/bouts/{id}/finish` | Finalizar pelea |
| POST | `/api/judge/bouts/{id}/score` | Enviar puntuación |

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| GET | `/api/auth/me` | Usuario actual |

---

## 🔌 WebSocket

### Endpoints
- `/ws` - Con SockJS
- `/ws-native` - WebSocket nativo

### Canales
- `/topic/bouts` - Actualizaciones de peleas
- `/topic/bout/{id}` - Pelea específica
- `/topic/bout/{id}/scores` - Puntuaciones

---

## 📁 Estructura

```
mma-demo-live/
├── src/main/java/com/example/mma/
│   ├── config/          # Configuraciones
│   ├── controller/      # REST Controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # Entidades JPA
│   ├── enums/           # Enumeraciones
│   ├── repository/      # Repositorios
│   ├── security/        # JWT y Auth
│   └── service/         # Lógica de negocio
├── src/main/resources/
│   ├── static/          # Frontend HTML/JS/CSS
│   └── application.properties
├── Procfile             # Para Heroku
├── system.properties    # Versión Java
└── pom.xml              # Dependencias
```

---

## ⚠️ Solución de Problemas

### Puerto ocupado
```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 8081).OwningProcess | Stop-Process -Force
```

### Error de compilación
```powershell
.\mvnw.cmd clean install -DskipTests
```

### Error CORS
El sistema ya tiene CORS configurado para aceptar cualquier origen.

---

## 📞 Soporte

Para problemas o preguntas, verificar:
1. Java 17+ instalado
2. Maven configurado
3. Puerto 8081 disponible
4. Logs en consola para errores específicos
