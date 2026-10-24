# Resolvitos Taxi Deliveries — Plan de Implementación de la Súper App

**Resolvitos Taxi Deliveries** ("Resolvitos") es una súper aplicación todo-en-uno 100% nativa para Android que integra Marketplace de Comercios y Restaurantes, Delivery de última milla, Movilidad urbana en **Moto Taxi (que también opera la flota de Delivery)** y **Taxi Económico** con selección de ruta en mapa y cálculo automático de tarifas por conductor, Fidelización con **Puntos, Misiones por Negocio y Premios Desbloqueables**, Comunicación Multimedia In-App y Directorio de Emergencias 24/7, adaptada a la economía bimonetaria (USD y Bolívares calculados automáticamente a la tasa oficial BCV) y respaldada en tiempo real por **Firebase Authentication y Cloud Firestore**.

---

## Decisiones Confirmadas y Revisión del Usuario

> [!IMPORTANT]
> A continuación se resumen las decisiones clave confirmadas por el usuario y los ajustes incorporados en esta revisión:

- **Base de Datos y Sincronización en Tiempo Real Confirmada**: Se utilizará **Firebase Cloud Firestore** y **Firebase Authentication (Google Sign-In con Credential Manager)**, manteniendo todas las colecciones y flujos reactivos en tiempo real (`profiles`, `comercios`, `drivers`, `orders`, `transport_requests`, `transport_bids`, `driver_locations`, `messages`, `products`, `common_products`, `banners`, `global_categories`, `reviews`, `business_missions`, `user_mission_progress`, `restaurant_rewards`, `global_prizes`, `emergency_services`, `support_tickets`, `notifications`, `app_branding`, `app_sound_settings`, `app_legal_and_share_settings`, `system_settings`, `admin_authorized_devices`, `restaurant_tables`, `waiters`, `cashiers`, `printers`, `expenses`, `cash_registers`, `lost_items`, `driver_commission_payments`, `payouts_history`).
- **Selección en Mapa Interactivo y Cálculo Automático de Tarifas por Conductor (Solo Moto Taxi y Taxi Económico)**:
  - **Únicamente 2 Modalidades de Vehículo**:
    1. **Moto Taxi (`moto`)**: Realiza traslados rápidos de pasajeros y además conforma la **flota oficial de Delivery y Mandados/Encomiendas** para comercios y usuarios.
    2. **Taxi Económico (`taxi_economico`)**: Vehículos sedán/compactos para traslado accesible de pasajeros. *(Se elimina la categoría Comfort).*
  - **Mapa Interactivo para Elegir Origen y Destino**: El cliente selecciona tocando o desplazando el marcador directamente en el mapa interactivo la ubicación exacta a donde desea ir (`origin` y `destination` con coordenadas, dirección y trazado de ruta en vivo).
  - **Cálculo de Monto según Tarifas Configuradas por cada Conductor/Taxi**: Cada conductor de Moto Taxi o Taxi Económico configura sus propias tarifas en su perfil (`base_fare_usd`, `rate_per_km_usd`, `minimum_fare_usd`). Al marcar el destino en el mapa, la app calcula automáticamente la distancia en kilómetros y muestra al cliente el monto estimado en **USD y Bs. (BCV)** desglosado según las tarifas configuradas por los conductores disponibles en línea, permitiendo elegir conductor o recibir sus ofertas en vivo.
- **Familia Tipográfica Oficial: Poppins**:
  - Se utilizará exclusivamente la familia tipográfica **Poppins** (en sus pesos Regular, Medium, SemiBold, Bold y ExtraBold) en toda la aplicación, instalada localmente en los recursos de Android para garantizar una estética geométrica, moderna y uniforme.
- **Sistema de Fidelización por Puntos, Misiones y Premios Desbloqueables (Sin Rifas)**:
  - Se elimina cualquier módulo o referencia a rifas o sorteos.
  - Se mantiene y potencia el sistema de **Puntos (`points` y `restaurant_points`)** orientado a **Misiones por Negocio y Globales**: los clientes ganan puntos y avanzan en misiones de cada comercio para **desbloquear premios y recompensas exclusivas**, configurables tanto por el **Administrador de cada Negocio (Modo Comercio)** como por el **Superadministrador (CPanel)**.
- **Cuenta Unificada Multi-Rol (Multi-Role Account)**: Un mismo usuario inicia sesión y, según los perfiles activados en su cuenta (`Cliente`, `Conductor` y/o `Comercio`), puede alternar de modo en caliente mediante un **Switch de Perfil** fluido sin cerrar sesión ni crear cuentas separadas. El modo **Superadministrador (CPanel)** se habilita para perfiles con `role = "admin"`, validando el dispositivo en `admin_authorized_devices` y exigiendo el `admin_security_pin` de doble seguridad.

> [!NOTE]
> **Entorno de Compilación Android Nativo**: La plataforma de compilación y emulación en vivo de Google AI Studio opera sobre **Kotlin y Jetpack Compose (Material Design 3)** para generar el APK nativo de Android. Toda la arquitectura reactiva, navegación protegida por roles, identidad visual `#FFD100` / `#0F172A`, familia tipográfica **Poppins** y conexión en tiempo real con Firebase Firestore se construirán de forma 100% nativa en Android con Jetpack Compose y Coroutines/StateFlow.

---

## 1. Visión General y Concepto Central

- **Qué Hace**: Resuelve en una sola pantalla las necesidades diarias de movilidad urbana (**Moto Taxi y Taxi Económico**), compras en restaurantes y comercios locales con **Delivery en Moto Taxi**, envíos de encomiendas o diligencias, fidelización gamificada mediante misiones con premios desbloqueables y asistencia de emergencias 24/7, mostrando cada monto simultáneamente en **Dólares ($ USD)** y **Bolívares (Bs.)** sincronizados en vivo con la tasa oficial BCV almacenada en `system_settings`.
- **Público Objetivo y Roles**:
  - **Clientes**: Compran en negocios locales con entrega a domicilio (vía Moto Taxi), retiro (pickup) o consumo en mesa; seleccionan en el mapa interactivo a dónde quieren ir en Moto Taxi o Taxi Económico viendo el cálculo inmediato de tarifa según cada conductor; envían mandados; acumulan puntos por negocio, completan misiones y desbloquean premios.
  - **Conductores / Repartidores (Moto Taxi y Taxi Económico)**: Alternan su disponibilidad en línea, **configuran sus propias tarifas base y por kilómetro**, reciben solicitudes de viaje o de delivery de comercios (en caso de Moto Taxi), envían contraofertas en tiempo real, actualizan su telemetría GPS, controlan sus vehículos y liquidan comisiones.
  - **Comercios Aliados**: Administran su catálogo y variantes, validan stock y pagos móviles, coordinan preparación y despacho conectado con la flota de Moto Taxis de Resolvitos, **configuran las misiones y premios desbloqueables de su propio negocio**, y operan su restaurante físico (mesas QR, mesoneros, caja y gastos).
  - **Superadministradores**: Auditan el mapa de flota en tiempo real, aprueban comercios y conductores, concilian comisiones, actualizan la tasa BCV oficial, **configuran misiones y premios globales o de cualquier negocio**, ajustan sonidos/banners y editan los documentos legales en vivo.
- **Valor Diferencial**: Mapa interactivo con cálculo transparente de tarifas personalizadas por conductor y distancia, unificación eficiente de Moto Taxi para pasajeros y delivery de última milla, motor bimonetario automático (`DualPrice` + Calculadora BCV interactiva) y fidelización real por comercio mediante misiones gamificadas configurables.

---

## 2. Experiencia de Usuario y Diseño Visual

### Flujos de Usuario Clave (Ciclos Operativos A–E)

1. **Autenticación, Términos Legales y Switch Multi-Rol**:
   - Al abrir la app, se verifica la conectividad en vivo (`OfflineIndicator`) y el bloqueo biométrico opcional (`biometric_lock_enabled`).
   - Antes del registro o desde el perfil de cada rol, el usuario puede abrir el visor Markdown en tiempo real conectado a `app_legal_and_share_settings (id = 'current')` que muestra `terms_client`, `terms_driver` o `terms_business` según el rol activo.
   - Una barra superior inteligente muestra la **Tasa BCV del día**, el **Selector de Ciudad/Estado (`CitySelectorModal`)**, el acceso a la **Calculadora BCV**, el botón **SOS / Emergencias 24/7** y el **Switch de Perfil Multi-Rol** (`Cliente` ⇄ `Conductor` ⇄ `Comercio` ⇄ `CPanel Admin` con guardia de PIN y dispositivo autorizado).
2. **Ciclo A — Marketplace Bimonetario y Delivery con Moto Taxis (`orders`)**:
   - Exploración por `banners` promocionales y `global_categories`, filtrando comercios activos y verificados (`is_active`, `is_approved`, `is_visible`), con insignias de financiamiento (`Cashea` / `Cuotas 2x3`) y vista previa de las **Misiones y Premios activos del comercio**.
   - Cada producto renderiza su componente `DualPrice` (`$ USD` + `Bs. BCV`). Al agregar al carrito, el cliente elige modalidad (`delivery`, `pickup`, `mesa`), selecciona en el mapa su ubicación de entrega, elige método de pago (`Pago Móvil`, `Efectivo`, `Transferencia`, `Cashea`, `Cuotas`), sube comprobante y referencia (`payment_proof_url`, `payment_reference`, `restaurant_payment_client_confirmed = true`).
   - El comercio confirma inventario (`stock_confirmed`), verifica el pago (`payment_status = 'verified'`), asigna tiempo de preparación (`preparing`) y despacha con repartidor propio (`own_delivery`) o solicita un **Moto Taxi de la flota Resolvitos** (`is_external_delivery = true`), lo que vincula automáticamente un documento en `transport_requests` (`vehicle_type = 'moto'`) y sincroniza los datos del conductor asignado.
   - El cliente sigue una barra de progreso en vivo (`accepted → preparing → ready → dispatched → delivered`) y, al completarse, recibe automáticamente sus **puntos del negocio y avance en las misiones activas**, además de calificar con estrellas y reseña (`reviews`).
3. **Ciclo B — Movilidad en Moto Taxi y Taxi Económico con Mapa Interactivo y Tarifas por Conductor (`transport_requests` y `transport_bids`)**:
   - **Selección en Mapa Interactivo**: El pasajero toca o arrastra el pin en el lienzo del mapa interactivo para fijar su **Origen** y la **Ubicación de Destino** a donde desea ir (o elige entre puntos frecuentes de la ciudad), visualizando la línea de ruta, distancia en kilómetros (`distance_km`) y tiempo estimado de viaje.
   - **Elección entre Moto Taxi y Taxi Económico + Cálculo Automático de Monto**:
     - El cliente alterna entre **Moto Taxi (`moto`)** y **Taxi Económico (`taxi_economico`)**.
     - El sistema consulta en tiempo real las tarifas configuradas por los conductores en línea (`base_fare_usd`, `rate_per_km_usd`, `minimum_fare_usd`) y calcula automáticamente el **monto a cobrar para esa distancia**, mostrando tanto la tarifa calculada de referencia como la lista de conductores cercanos con el monto exacto que cobra cada uno en **USD y Bs. (BCV)**.
   - **Solicitud Directa o Subasta en Vivo**:
     - Al confirmar el destino, la solicitud pasa a `'searching'` con el monto calculado (o ajustado por el pasajero). Los conductores en línea de la categoría seleccionada (`moto` o `taxi_economico`) ven el viaje en su radar y pueden aceptar la tarifa calculada o enviar su contraoferta (`transport_bids`) con `offered_price`, `estimated_eta_minutes`, placa, modelo y calificación.
   - **Aceptación y Seguimiento GPS**: Cuando el cliente acepta al conductor, se adjudica el viaje, el mapa muestra en vivo el desplazamiento del Moto Taxi o Taxi Económico (`driver_locations`), y se procesa el flujo `driver_arrived_at → en ruta → completed_at → confirmación de pago y comisión`, incluyendo el reporte de **Objetos Perdidos (`lost_items`)**.
4. **Ciclo C — Mandados, Encomiendas y Diligencias (vía Moto Taxi / Taxi Económico)**:
   - Selección en el mapa del punto de recogida y punto de entrega con cálculo automático del flete según la distancia y las tarifas de los Moto Taxis disponibles.
   - Captura descripción del paquete (`package_description`), instrucciones de la diligencia (`mandado_details`), nota de voz (`audio_url`), foto de referencia (`reference_url`), datos de remitente/destinatario y quién asume el envío (`flete_pagado_por: remitente | destinatario`).
5. **Ciclo D — Comunicación Multimedia In-App (Chat y Llamadas VoIP)**:
   - Dentro de cualquier pedido o viaje activo, un panel de **Chat Unificado (`messages`)** permite enviar texto, imágenes, notas de voz y video corto en tiempo real entre Cliente, Conductor y Comercio.
   - Incluye pantalla y señalización en vivo de **Llamada de Voz In-App (`InAppCall`)** a través de Firestore para comunicarse sin exponer números telefónicos.
6. **Ciclo E — Puntos, Misiones por Negocio, Premios Desbloqueables, Referidos y Emergencias 24/7**:
   - **Centro de Misiones y Premios**: El cliente consulta su saldo de puntos globales (`points`) y puntos específicos por cada negocio (`restaurant_points`), junto con las barras de progreso de sus **Misiones por Negocio**.
   - **Desbloqueo y Canje de Premios**: Al alcanzar la meta de puntos o completar una misión de un negocio, se desbloquea automáticamente el premio configurado (`restaurant_rewards` / `global_prizes`), permitiendo al cliente reclamarlo e incluirlo en su próximo pedido.
   - **Gestión de Misiones y Premios por Comercio y Superadmin**: Configurables en vivo tanto desde el **Modo Comercio** (para su propio local) como desde el **Modo Superadministrador (CPanel)** (para toda la app o cualquier negocio).
   - **Referidos y Emergencias 24/7**: Invitación de amigos con `referral_code` (`referral_contests`) y **Directorio de Emergencias 24/7 (`emergency_services`)** con filtro local/nacional y disparo directo de llamada telefónica o WhatsApp.

### Identidad Visual, Paleta y Tipografía Poppins

- **Dirección Estética**: Estética de Súper App urbana de alto contraste, cálida y ultrarrápida, diseñada para lectura instantánea bajo luz solar o en conducción nocturna.
- **Paleta de Colores Oficial**:
  - **Amarillo Resolvitos (Primario)**: `#FFD100` (botones principales, cabeceras hero, pines de destino en el mapa, insignias BCV, indicadores de misiones desbloqueadas y splash).
  - **Slate Profundo (Secundario / Contraste)**: `#0F172A` (texto principal sobre amarillo, barra de navegación inferior, paneles de modo conductor nocturno y cabeceras del CPanel).
  - **Fondos de Interfaz**: `#FFFFFF` (Blanco puro para tarjetas elevadas) y `#F8FAFC` (Gris hielo para fondos generales).
  - **Semáforo Operativo de Estados**:
    - Verde Esmeralda `#10B981`: Confirmado, En Línea, Misión Completada / Premio Desbloqueado, Entregado.
    - Ámbar `#F59E0B`: Pendiente, Preparando, Misión en Progreso, Buscando Conductor.
    - Rojo Coral `#EF4444`: Cancelado, Rechazado, Deuda Vencida, SOS Emergencia.
- **Tipografía y Jerarquía (Familia Poppins Completa)**:
  - Toda la aplicación utilizará la familia tipográfica **Poppins** instalada localmente en `res/font/poppins.ttf` mediante `font-util`:
    - **Poppins ExtraBold / Bold**: Títulos de pantallas, montos bimonetarios (`DualPrice`), calculadoras de tarifas en el mapa e indicadores de misión.
    - **Poppins SemiBold / Medium**: Botones de acción, pestañas de rol, nombres de comercios, placas de vehículos y etiquetas de estado.
    - **Poppins Regular**: Descripciones de productos, mensajes de chat, direcciones en el mapa y términos legales.
- **Retroalimentación Sensorial (Haptics y Sonidos Dinámicos)**:
  - Motor integrado de efectos sonoros (`SoundAndHapticManager`) sincronizado con `app_sound_settings` y `ToneGenerator`/`HapticFeedback` nativo de Android para cada hito del ciclo operativo: `alert_loop`, `chat_pop`, `sos_siren`, `ticket_bell`, `push_chime`, `cycle_accept`, `cycle_ready`, `cycle_driver`, `cycle_transit`, `cycle_arrived`, `done_success` y `done_cash`.

---

## 3. Decisiones de Producto y Balance Arquitectónico

- **Decisión 1: Mapa Interactivo con Motor de Tarifación por Conductor (`Moto Taxi` y `Taxi Económico`)**
  - *Enfoque Elegido*: Un componente de mapa vectorial interactivo (`InteractiveRouteMapCanvas`) permite al usuario tocar cualquier punto de la ciudad o arrastrar el pin de destino, calculando en tiempo real la distancia en kilómetros mediante fórmula geodésica (Haversine) y aplicando la fórmula de cada conductor (`max(minimum_fare_usd, base_fare_usd + distance_km * rate_per_km_usd)`). Además, en el **Modo Conductor**, cada chofer de Moto Taxi o Taxi Económico cuenta con un panel para configurar su tarifa base, tarifa por km y tarifa mínima.
  - *Por Qué*: Garantiza que el cliente conozca de inmediato cuánto se le cobrará al seleccionar su destino según las tarifas reales de los Moto Taxis y Taxis Económicos disponibles, además de permitir que los mismos Moto Taxis cubran los despachos de Delivery de los comercios.
- **Decisión 2: Cuenta Unificada Multi-Rol con Activación Instantánea de Perfiles**
  - *Enfoque Elegido*: Un único documento de perfil en `profiles/{uid}` almacena los roles habilitados (`enabled_roles: ["client", "driver", "business", "admin"]`) y el rol activo actual (`active_role`), permitiendo activar el perfil de Conductor (eligiendo si es Moto Taxi/Delivery o Taxi Económico) o registrar un Comercio Aliado desde la misma cuenta y cambiar de vista con un solo toque.
  - *Por Qué*: Facilita tanto la experiencia real de usuarios multi-rol como la prueba inmediata de los 4 flujos interconectados en tiempo real.
- **Decisión 3: Motor de Misiones y Premios Desbloqueables Configurable por Negocio y Superadmin**
  - *Enfoque Elegido*: Las misiones y recompensas se almacenan en Firestore (`business_missions` y `restaurant_rewards` / `global_prizes`) vinculadas a un `comercio_id` (o `"global"`). Cada vez que un pedido se entrega o se acreditan puntos, se evalúa el progreso del usuario en `user_mission_progress`, desbloqueando los premios cumplidos en tiempo real.
  - *Por Qué*: Empodera a cada comercio aliado y al Superadministrador para diseñar estrategias de fidelización medibles y atractivas.

---

## 4. Arquitectura Técnica y Estrategia de Datos

### Diagrama de Arquitectura y Jerarquía de Componentes

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                        CAPA DE DATOS Y SERVICIOS EN VIVO                         │
│  ┌─────────────────────────┐  ┌──────────────────────────┐  ┌─────────────────┐  │
│  │ Firebase Auth           │  │ Cloud Firestore Realtime │  │ Motor Sensorial │  │
│  │ (Google Sign-In +       │  │ (Colecciones Public +    │  │ (Haptics +      │  │
│  │  Guardia PIN Superadmin)│  │  Snapshots en Vivo)      │  │  Sonidos Evento)│  │
│  └────────────┬────────────┘  └─────────────┬────────────┘  └────────┬────────┘  │
└───────────────┼─────────────────────────────┼────────────────────────┼───────────┘
                │                             │                        │
                ▼                             ▼                        ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│                 REPOSITORIOS Y VIEWMODELS REACTIVOS (STATEFLOW)                  │
│  ┌────────────────────────────────────────────────────────────────────────────┐  │
│  │ ResolvitosRepository + SuperAppViewModel                                   │  │
│  │ • Motor Bimonetario (Tasa BCV en vivo + DualPrice + Calculadora BCV)       │  │
│  │ • Motor de Mapa y Tarifación por Conductor (Moto Taxi / Taxi Económico)    │  │
│  │ • Motor de Puntos, Misiones por Negocio y Premios Desbloqueables           │  │
│  │ • Sincronizador Orden Comercio ⇄ Flota Moto Taxi Delivery ⇄ Subasta Bids   │  │
│  └─────────────────────────────────────┬──────────────────────────────────────┘  │
└────────────────────────────────────────┼─────────────────────────────────────────┘
                                         │
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│        CAPA DE NAVEGACIÓN Y VISTAS MULTI-ROL (JETPACK COMPOSE + POPPINS)         │
│  ┌────────────────────────────────────────────────────────────────────────────┐  │
│  │ Barra Superior Global: Ciudad | Tasa BCV + Calculadora | SOS | Switch Rol  │  │
│  │ Indicador de Conexión (OfflineIndicator) + Bloqueo Biométrico/PIN          │  │
│  └──────┬──────────────────────┬───────────────────────┬───────────────┬──────┘  │
│         │                      │                       │               │         │
│         ▼                      ▼                       ▼               ▼         │
│  ┌──────────────┐      ┌───────────────┐       ┌──────────────┐ ┌─────────────┐  │
│  │ MODO CLIENTE │      │ MODO CONDUCTOR│       │ MODO COMERCIO│ │ MODO ADMIN  │  │
│  │ • Marketplace│      │ • Moto Taxi & │       │ • Pedidos    │ │ • CPanel PIN│  │
│  │ • Carrito BCV│      │   Taxi Económ.│       │ • Despacho   │ │ • Tasa BCV  │  │
│  │ • Mapa Destin│      │ • Config Tarif│       │   Moto Taxi  │ │ • Flota GPS │  │
│  │   Moto/Taxi  │      │ • Radar/Pujas │       │ • Catálogo   │ │ • Misiones y│  │
│  │ • Mandados   │      │ • Delivery App│       │ • Misiones y │ │   Premios   │  │
│  │ • Misiones y │      │ • Vehículos y │       │   Premios    │ │ • Legal/SOS │  │
│  │   Premios    │      │   Comisiones  │       │ • Mesas/Caja │ │             │  │
│  └──────┬───────┘      └───────┬───────┘       └───────┬──────┘ └──────┬──────┘  │
│         └──────────────────────┴───────────┬───────────┴───────────────┘         │
│                                            ▼                                     │
│  ┌────────────────────────────────────────────────────────────────────────────┐  │
│  │ MÓDULOS TRANSVERSALES COMPARTIDOS                                          │  │
│  │ • Chat Multimedia en Vivo (Texto, Foto, Audio, Video) + Llamada VoIP       │  │
│  │ • Visor Markdown Términos Legales (Cliente/Driver/Comercio) + Borrar Cuenta│  │
│  │ • Directorio Emergencias 24/7 + Objetos Perdidos + Tickets de Soporte      │  │
│  └────────────────────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

### Modelo de Datos en Cloud Firestore y Relaciones Clave

1. **Configuración Global y Seguridad (`system_settings`, `app_legal_and_share_settings`, `app_sound_settings`, `admin_authorized_devices`)**:
   - `system_settings/current`: Almacena `bcv_rate` (tasa oficial Bs./USD), tarifas sugeridas por defecto para Moto Taxi y Taxi Económico, porcentajes de comisión y parámetros de puntos por dólar/pedido.
   - `app_legal_and_share_settings/current`: Contiene el Markdown en vivo de `terms_client`, `terms_driver` y `terms_business`.
   - `admin_authorized_devices/{deviceId}`: Registra el identificador del dispositivo autorizado junto con el `admin_security_pin` requerido para desbloquear el CPanel.
2. **Identidad Multi-Rol y Tarifas por Conductor (`profiles`, `drivers`, `driver_locations`, `comercios`)**:
   - `profiles/{uid}`: Perfil maestro del usuario con `role`, `enabled_roles`, `city`, `points` (puntos globales), `restaurant_points` (mapa de puntos acumulados por `comercio_id`), `referral_code`, `biometric_lock_enabled` y `admin_security_pin`.
   - `drivers/{driverId}` y `driver_locations/{driverId}`:
     - Modalidad de vehículo (`vehicle_type`: `'moto'` para Moto Taxi + Delivery, o `'taxi_economico'` para Taxi Económico).
     - **Tarifas Configuradas por el Conductor**: `base_fare_usd` (banderazo base), `rate_per_km_usd` (costo por kilómetro recorrido), `minimum_fare_usd` (carrera mínima) y `delivery_flat_fee_usd` (tarifa de delivery para Moto Taxi).
     - Estado `is_online`, `availability` (`disponible`, `ocupado`, `fuera_de_linea`), coordenadas GPS (`latitude`, `longitude`, `current_heading`, `current_speed`), bolso térmico (`has_thermal_bag`), deuda de comisiones (`commission_debt`) y vehículos registrados (`registered_vehicles`).
   - `comercios/{comercioId}`: Datos del negocio, propietario (`owner_id`), estado de verificación (`verification_status`), horarios (`working_hours`), tarifas (`delivery_rates`), banderas de financiamiento (`has_cashea`, `has_two_by_three`), catálogo (`products`), mesas (`restaurant_tables`), personal (`waiters`, `cashiers`), gastos (`expenses`) y cierres (`cash_registers`).
3. **Operación Transaccional (`orders`, `transport_requests`, `transport_bids`, `messages`, `reviews`, `lost_items`)**:
   - `orders/{orderId}`: Ciclo completo de pedido de comida/productos con montos en USD y tasa BCV aplicada, confirmación de stock (`stock_confirmed`, `missing_items`), validación de pago móvil (`payment_proof_url`, `payment_reference`, `payment_status`), vínculo con delivery externo en Moto Taxi (`is_external_delivery`, `transport_request_id`, datos del Moto Taxi sincronizados) y acreditación de puntos y misiones (`points_credited`).
   - `transport_requests/{requestId}` y `transport_bids/{bidId}`: Solicitudes de **Moto Taxi (`moto`)**, **Taxi Económico (`taxi_economico`)**, **Delivery de Comercio** o **Mandado/Encomienda** con coordenadas de `origin` y `destination` seleccionadas en el mapa, `distance_km`, `price` calculado según las tarifas del conductor y subasta en tiempo real de ofertas (`transport_bids`).
   - `messages/{messageId}`: Mensajes en tiempo real vinculados por `order_id` o `request_id` con soporte multimedia y eventos de llamada VoIP (`InAppCall`).
4. **Fidelización por Misiones, Premios y Soporte (`business_missions`, `user_mission_progress`, `restaurant_rewards`, `global_prizes`, `emergency_services`, `support_tickets`)**:
   - `business_missions/{missionId}` y `restaurant_rewards/{rewardId}`: Misiones y premios asociados a cada `comercio_id` (o globales en `global_prizes`), editables tanto por el administrador del negocio propietario como por el Superadministrador.
   - `user_mission_progress/{progressId}`: Progreso en tiempo real del cliente por misión y negocio (`current_points`, `current_orders`, `is_unlocked`, `is_claimed`).

### Mapeo de Componentes Interactivos y Mutación de Estado

- **Selector de Ubicación en Mapa Interactivo y Cotizador por Conductor**:
  - En la pantalla de Movilidad y Mandados, el cliente interactúa directamente con el mapa (`InteractiveRouteMapCanvas`): toca o mueve el marcador para fijar el punto exacto de destino (o elige zonas rápidas en el mapa), y el sistema traza la ruta desde su origen, calcula los kilómetros (`distance_km`) y muestra al instante cuánto le cobrará cada **Moto Taxi** o **Taxi Económico** en línea según sus tarifas configuradas (`base_fare_usd + distance_km * rate_per_km_usd`), tanto en **USD ($)** como en **Bolívares (Bs. BCV)**.
- **Panel de Configuración de Tarifas del Conductor (Moto Taxi y Taxi Económico)**:
  - En el **Modo Conductor**, el chofer define si opera como **Moto Taxi (Pasajeros + Delivery)** o **Taxi Económico**, y ajusta en tiempo real sus tarifas (`Tarifa Mínima USD`, `Arranque Base USD`, `Costo por Km USD`), las cuales se reflejan inmediatamente en el calculador del mapa de los clientes.
- **Ciclo Completo del Carrito, Despacho Conectado con Moto Taxi y Avance de Misiones**:
  - El cliente agrega productos con modificadores, marca su ubicación de entrega en el mapa, adjunta referencia de Pago Móvil y envía la orden (`pending` + `cycle_accept`).
  - El comercio cambia el estado a `preparing` y pulsa *"Solicitar Moto Taxi Delivery"*, creando automáticamente el `transport_request` para la flota de Moto Taxis.
  - En el Modo Conductor, el Moto Taxi acepta el envío (`cycle_driver`), actualiza su estado a en camino (`cycle_transit`) y entregado (`done_success`), acreditando puntos al cliente en ese negocio y desbloqueando sus premios de misión.
- **Configurador y Desbloqueo de Misiones y Premios (Cliente, Comercio y Superadmin)**:
  - Gestión interactiva de misiones y premios desbloqueables desde el Modo Comercio y el CPanel Superadmin, y reclamo inmediato desde el Modo Cliente.
- **Gestión Interna del Restaurante (Mesas QR, Caja y Gastos)** y **Guardia de Doble Seguridad del Superadministrador**:
  - Módulos completos para control de mesas QR, mesoneros, gastos y cierres de caja en Modo Comercio, y desbloqueo por PIN y dispositivo autorizado en el CPanel Superadmin.
