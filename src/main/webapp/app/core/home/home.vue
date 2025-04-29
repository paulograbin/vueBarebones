<template>
  <div class="home row">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <h1 class="display-4">Bem vindo, Java Hipster!</h1>
      <p class="lead">Esta é a página principal</p>

      <div>
        <div class="alert alert-success" v-if="authenticated">
          <span v-if="username">Você está logado como "{{ username }}".</span>
        </div>

        <div class="alert alert-warning" v-if="!authenticated">
          <span>Se deseja </span>
          <a class="alert-link" @click="openLogin()">entrar</a
          ><span
            >, utilize as seguintes contas padrões:<br />- Administrador (usuário="admin" and senha="admin") <br />- Usuário (usuário="user"
            e senha="user").</span
          >
        </div>
      </div>

      <p>Em caso de dúvida sobre o JHipster:</p>

      <ul>
        <li><a href="https://www.jhipster.tech/" target="_blank" rel="noopener noreferrer">Página principal JHipster</a></li>
        <li>
          <a href="https://stackoverflow.com/tags/jhipster/info" target="_blank" rel="noopener noreferrer">JHipster no Stack Overflow</a>
        </li>
        <li>
          <a href="https://github.com/jhipster/generator-jhipster/issues?state=open" target="_blank" rel="noopener noreferrer"
            >JHipster bug tracker</a
          >
        </li>
        <li>
          <a href="https://gitter.im/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer"
            >Sala de bate-papo pública JHipster</a
          >
        </li>
        <li>
          <a href="https://twitter.com/jhipster" target="_blank" rel="noopener noreferrer">siga @jhipster no Twitter</a>
        </li>
      </ul>

      <p>
        <span>Se você gosta do JHipster, não se esqueça de avaliar no</span>
        <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer">GitHub</a>!
      </p>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
