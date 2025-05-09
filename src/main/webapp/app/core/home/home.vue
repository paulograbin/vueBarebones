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
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
