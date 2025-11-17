<template>
  <main class="page">
    <section class="hero">
      <h1>HAP Metadata</h1>
      <p>
        Vue + Spring Boot starter shell for the metadata console. Use this placeholder
        UI while wiring the API.
      </p>
      <div class="actions">
        <a href="https://vitejs.dev" target="_blank" rel="noreferrer">Vite Docs</a>
        <a href="https://vuejs.org" target="_blank" rel="noreferrer">Vue Docs</a>
      </div>
    </section>
  </main>
</template>

<style scoped>
.page {
  font-family: 'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at top, #fef9f5, #f0f4ff);
  color: #1f2937;
  padding: 2rem;
}

.hero {
  max-width: 720px;
  text-align: center;
  background: white;
  padding: 3rem;
  border-radius: 1rem;
  box-shadow: 0 15px 40px rgba(15, 23, 42, 0.15);
}

.hero h1 {
  font-size: 2.75rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.15rem;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.actions a {
  text-decoration: none;
  border: 1px solid #6366f1;
  color: #4338ca;
  padding: 0.65rem 1.5rem;
  border-radius: 999px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.actions a:hover {
  background: #6366f1;
  color: white;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.35);
}
</style>
