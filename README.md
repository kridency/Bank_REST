<h1>🚀 Banking card management system</h1>
  
<h2>📝 Description</h2>
  <p>Banking card Java (Spring Boot) backend-application with following capabilities:</p>
  <ul>
    <li>Banking card record creation and management</li>
    <li>Card account status report</li>
    <li>Transfer between your own card accounts</li>
  </ul>

<h2>💳 Banking card details</h2>
  <ul>
    <li>Card number (displayed by the mask: <code>**** **** **** 1234</code>)</li>
    <li>Owner</li>
    <li>Validity period</li>
    <li>Status: Active, Blocked, Expired</li>
    <li>Balance</li>
  </ul>

<h2>🧾 Requirements</h2>

<h3>✅ Authentication и authorization</h3>
  <ul>
    <li>Spring Security + JWT</li>
    <li>User roles: <code>ADMIN</code> и <code>USER</code></li>
  </ul>

<h3>✅ Permissions</h3>
<strong>Administrator:</strong>
  <ul>
    <li>Create, block, activate, delete card account</li>
    <li>Manage users</li>
    <li>Access all cards information</li>
  </ul>

<strong>User:</strong>
  <ul>
    <li>Browse personal cards (search + pagination)</li>
    <li>Request account block</li>
    <li>Cash transfer between owned cards</li>
    <li>Balance report</li>
  </ul>

<h3>✅ API</h3>
  <ul>
    <li>CRUD for card records</li>
    <li>Transfer between personal cards</li>
    <li>Filter and pagination</li>
    <li>Validation and error messaging</li>
  </ul>

<h3>✅ Security</h3>
  <ul>
    <li>Data Encryption</li>
    <li>Role based authorization</li>
    <li>Card number masking</li>
  </ul>

<h3>✅ Database integration</h3>
  <ul>
    <li>PostgreSQL</li>
    <li>Liquibase migration (<code>src/main/resources/db/migration</code>)</li>
  </ul>

<h3>✅ Documentation</h3>
  <ul>
    <li>Swagger UI / OpenAPI — <code>docs/openapi.yaml</code></li>
    <li><code>README.md</code> с инструкцией запуска</li>
  </ul>

<h3>✅ Deployment and testing</h3>
  <ul>
    <li>Docker Compose for dev-environment</li>
    <li>Liquibase data migration</li>
    <li>Unit-tests</li>
  </ul>

<h2>💡 Frameworks</h2>
  <p>
    Java 17+, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Liquibase, Docker, JWT, Swagger (OpenAPI)
  </p>
