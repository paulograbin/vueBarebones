<template>
  <div class="modal-body">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <b-alert show data-cy="loginError" variant="danger" v-if="authenticationError"
          ><strong>Erro de autenticação!</strong> Por favor verifique suas credenciais e tente novamente.</b-alert
        >
      </div>
      <div class="col-md-8">
        <b-form @submit.prevent="doLogin()">
          <b-form-group label="Usuário" label-for="username">
            <b-form-input id="username" type="text" name="username" autofocus placeholder="Seu usuário" v-model="login" data-cy="username">
            </b-form-input>
          </b-form-group>
          <b-form-group label="Senha" label-for="password">
            <b-form-input id="password" type="password" name="password" placeholder="Sua senha" v-model="password" data-cy="password">
            </b-form-input>
          </b-form-group>
          <b-form-checkbox id="rememberMe" name="rememberMe" v-model="rememberMe" checked>
            <span>Manter-me logado</span>
          </b-form-checkbox>
          <div>
            <b-button data-cy="submit" type="submit" variant="primary">Entrar</b-button>
          </div>
        </b-form>
        <p></p>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./login-form.component.ts"></script>
