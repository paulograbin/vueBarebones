<template>
  <div>
    <h2>
      <span id="health-page-heading" data-cy="healthPageHeading">Estado do Sistema</span>
      <button class="btn btn-primary float-right" @click="refresh()" :disabled="updatingHealth">
        <font-awesome-icon icon="sync"></font-awesome-icon> <span>Atualizar</span>
      </button>
    </h2>
    <div class="table-responsive">
      <table id="healthCheck" class="table table-striped" aria-describedby="Health check">
        <thead>
          <tr>
            <th scope="col">Nome do Serviço</th>
            <th class="text-center" scope="col">Estado</th>
            <th class="text-center" scope="col">Detalhes</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="health of healthData" :key="health.name">
            <td>
              <span class="text-capitalize">{{ baseName(health.name) }}</span> {{ subSystemName(health.name) }}
            </td>
            <td class="text-center">
              <span class="badge" :class="getBadgeClass(health.status)">
                {{ health.status }}
              </span>
            </td>
            <td class="text-center">
              <a class="hand" @click="showHealth(health)" v-if="health.details || health.error">
                <font-awesome-icon icon="eye"></font-awesome-icon>
              </a>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="healthModal">
      <template #modal-title>
        <h4 v-if="currentHealth" class="modal-title" id="showHealthLabel">
          <span class="text-capitalize">{{ baseName(currentHealth.name) }}</span>
          {{ subSystemName(currentHealth.name) }}
        </h4>
      </template>
      <health-modal :current-health="currentHealth"></health-modal>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./health.component.ts"></script>
