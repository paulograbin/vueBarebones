<template>
  <div>
    <h2>
      <span id="metrics-page-heading" data-cy="metricsPageHeading">Métricas da aplicação</span>
      <button class="btn btn-primary float-right" @click="refresh()">
        <font-awesome-icon icon="sync"></font-awesome-icon> <span>Atualizar</span>
      </button>
    </h2>

    <h3>Métricas da JVM</h3>
    <div class="row" v-if="!updatingMetrics">
      <div class="col-md-4">
        <h4>Memória</h4>
        <div>
          <div v-for="(entry, key) of metrics.jvm" :key="key">
            <span v-if="entry.max !== -1">
              <span>{{ key }}</span> ({{ formatNumber1(entry.used / 1048576) }}M / {{ formatNumber1(entry.max / 1048576) }}M)
            </span>
            <span v-else>
              <span>{{ key }}</span> {{ formatNumber1(entry.used / 1048576) }}M
            </span>
            <div>Committed : {{ formatNumber1(entry.committed / 1048576) }}M</div>
            <b-progress v-if="entry.max !== -1" variant="success" animated :max="entry.max" striped>
              <b-progress-bar :value="entry.used" :label="formatNumber1((entry.used * 100) / entry.max) + '%'"> </b-progress-bar>
            </b-progress>
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <h4>Threads</h4>
        <span><span>Em execução</span> {{ threadStats.threadDumpRunnable }}</span>
        <b-progress variant="success" :max="threadStats.threadDumpAll" striped>
          <b-progress-bar
            :value="threadStats.threadDumpRunnable"
            :label="formatNumber1((threadStats.threadDumpRunnable * 100) / threadStats.threadDumpAll) + '%'"
          >
          </b-progress-bar>
        </b-progress>

        <span><span>Tempo de espera</span> ({{ threadStats.threadDumpTimedWaiting }})</span>
        <b-progress variant="success" :max="threadStats.threadDumpAll" striped>
          <b-progress-bar
            :value="threadStats.threadDumpTimedWaiting"
            :label="formatNumber1((threadStats.threadDumpTimedWaiting * 100) / threadStats.threadDumpAll) + '%'"
          >
          </b-progress-bar>
        </b-progress>

        <span><span>Aguardando</span> ({{ threadStats.threadDumpWaiting }})</span>
        <b-progress variant="success" :max="threadStats.threadDumpAll" striped>
          <b-progress-bar
            :value="threadStats.threadDumpWaiting"
            :label="formatNumber1((threadStats.threadDumpWaiting * 100) / threadStats.threadDumpAll) + '%'"
          >
          </b-progress-bar>
        </b-progress>

        <span><span>Bloqueado</span> ({{ threadStats.threadDumpBlocked }})</span>
        <b-progress variant="success" :max="threadStats.threadDumpAll" striped>
          <b-progress-bar
            :value="threadStats.threadDumpBlocked"
            :label="formatNumber1((threadStats.threadDumpBlocked * 100) / threadStats.threadDumpAll) + '%'"
          >
          </b-progress-bar>
        </b-progress>

        <span
          >Total: {{ threadStats.threadDumpAll }}
          <a class="hand" v-b-modal.metricsModal data-toggle="modal" @click="openModal()" data-target="#threadDump">
            <font-awesome-icon icon="eye"></font-awesome-icon>
          </a>
        </span>
      </div>
      <div class="col-md-4">
        <h4>System</h4>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-4">Uptime</div>
          <div class="col-md-8 text-right">{{ convertMillisecondsToDuration(metrics.processMetrics['process.uptime']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-4">Start time</div>
          <div class="col-md-8 text-right">{{ formatDate(metrics.processMetrics['process.start.time']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">Process CPU usage</div>
          <div class="col-md-3 text-right">{{ formatNumber2(100 * metrics.processMetrics['process.cpu.usage']) }} %</div>
        </div>
        <b-progress variant="success" :max="100" striped>
          <b-progress-bar
            :value="100 * metrics.processMetrics['process.cpu.usage']"
            :label="formatNumber1(100 * metrics.processMetrics['process.cpu.usage']) + '%'"
          >
          </b-progress-bar>
        </b-progress>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">System CPU usage</div>
          <div class="col-md-3 text-right">{{ formatNumber2(100 * metrics.processMetrics['system.cpu.usage']) }} %</div>
        </div>
        <b-progress variant="success" :max="100" striped>
          <b-progress-bar
            :value="100 * metrics.processMetrics['system.cpu.usage']"
            :label="formatNumber1(100 * metrics.processMetrics['system.cpu.usage']) + '%'"
          >
          </b-progress-bar>
        </b-progress>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">System CPU count</div>
          <div class="col-md-3 text-right">{{ metrics.processMetrics['system.cpu.count'] }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">System 1m Load average</div>
          <div class="col-md-3 text-right">{{ formatNumber2(metrics.processMetrics['system.load.average.1m']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">Process files max</div>
          <div class="col-md-3 text-right">{{ formatNumber1(metrics.processMetrics['process.files.max']) }}</div>
        </div>
        <div class="row" v-if="!updatingMetrics">
          <div class="col-md-9">Process files open</div>
          <div class="col-md-3 text-right">{{ formatNumber1(metrics.processMetrics['process.files.open']) }}</div>
        </div>
      </div>
    </div>

    <h3>Coletor de lixo</h3>
    <div class="row" v-if="!updatingMetrics && isObjectExisting(metrics, 'garbageCollector')">
      <div class="col-md-4">
        <div>
          <span>
            GC Live Data Size/GC Max Data Size ({{ formatNumber1(metrics.garbageCollector['jvm.gc.live.data.size'] / 1048576) }}M /
            {{ formatNumber1(metrics.garbageCollector['jvm.gc.max.data.size'] / 1048576) }}M)
          </span>
          <b-progress variant="success" :max="metrics.garbageCollector['jvm.gc.max.data.size']" striped>
            <b-progress-bar
              :value="metrics.garbageCollector['jvm.gc.live.data.size']"
              :label="
                formatNumber2(
                  (100 * metrics.garbageCollector['jvm.gc.live.data.size']) / metrics.garbageCollector['jvm.gc.max.data.size'],
                ) + '%'
              "
            >
            </b-progress-bar>
          </b-progress>
        </div>
      </div>
      <div class="col-md-4">
        <div>
          <span>
            GC Memory Promoted/GC Memory Allocated ({{ formatNumber1(metrics.garbageCollector['jvm.gc.memory.promoted'] / 1048576) }}M /
            {{ formatNumber1(metrics.garbageCollector['jvm.gc.memory.allocated'] / 1048576) }}M)
          </span>
          <b-progress variant="success" :max="metrics.garbageCollector['jvm.gc.memory.allocated']" striped>
            <b-progress-bar
              :value="metrics.garbageCollector['jvm.gc.memory.promoted']"
              :label="
                formatNumber2(
                  (100 * metrics.garbageCollector['jvm.gc.memory.promoted']) / metrics.garbageCollector['jvm.gc.memory.allocated'],
                ) + '%'
              "
            >
            </b-progress-bar>
          </b-progress>
        </div>
      </div>
      <div class="col-md-4">
        <div class="row">
          <div class="col-md-9">Classes loaded</div>
          <div class="col-md-3 text-right">{{ metrics.garbageCollector.classesLoaded }}</div>
        </div>
        <div class="row">
          <div class="col-md-9">Classes unloaded</div>
          <div class="col-md-3 text-right">{{ metrics.garbageCollector.classesUnloaded }}</div>
        </div>
      </div>
      <div class="table-responsive">
        <table class="table table-striped" aria-describedby="Jvm gc">
          <thead>
            <tr>
              <th scope="col"></th>
              <th scope="col" class="text-right">Contagem</th>
              <th scope="col" class="text-right">Mediana</th>
              <th scope="col" class="text-right">Min</th>
              <th scope="col" class="text-right">p50</th>
              <th scope="col" class="text-right">p75</th>
              <th scope="col" class="text-right">p95</th>
              <th scope="col" class="text-right">p99</th>
              <th scope="col" class="text-right">Max</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>jvm.gc.pause</td>
              <td class="text-right">{{ metrics.garbageCollector['jvm.gc.pause'].count }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause'].mean) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.0']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.5']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.75']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.95']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause']['0.99']) }}</td>
              <td class="text-right">{{ formatNumber2(metrics.garbageCollector['jvm.gc.pause'].max) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <h3>Requisições HTTP (eventos em milissegundo)</h3>
    <table
      class="table table-striped"
      v-if="!updatingMetrics && isObjectExisting(metrics, 'http.server.requests')"
      aria-describedby="Jvm http"
    >
      <thead>
        <tr>
          <th scope="col">Código</th>
          <th scope="col">Contagem</th>
          <th scope="col" class="text-right">Mediana</th>
          <th scope="col" class="text-right">Máxima</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(entry, key) of metrics['http.server.requests']['percode']" :key="key">
          <td>{{ key }}</td>
          <td>
            <b-progress variant="success" animated :max="metrics['http.server.requests']['all'].count" striped>
              <b-progress-bar :value="entry.count" :label="formatNumber1(entry.count)"></b-progress-bar>
            </b-progress>
          </td>
          <td class="text-right">
            {{ formatNumber2(filterNaN(entry.mean)) }}
          </td>
          <td class="text-right">{{ formatNumber2(entry.max) }}</td>
        </tr>
      </tbody>
    </table>

    <h3>Endpoints requests (time in millisecond)</h3>
    <div class="table-responsive" v-if="!updatingMetrics">
      <table class="table table-striped" aria-describedby="Endpoint">
        <thead>
          <tr>
            <th scope="col">Method</th>
            <th scope="col">Endpoint url</th>
            <th scope="col" class="text-right">Count</th>
            <th scope="col" class="text-right">Mean</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="(entry, entryKey) of metrics.services">
            <tr v-for="(method, methodKey) of entry" :key="entryKey + '-' + methodKey">
              <td>{{ methodKey }}</td>
              <td>{{ entryKey }}</td>
              <td class="text-right">{{ method.count }}</td>
              <td class="text-right">{{ formatNumber2(method.mean) }}</td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <h3>Estatísticas da cache</h3>
    <div class="table-responsive" v-if="!updatingMetrics && isObjectExisting(metrics, 'cache')">
      <table class="table table-striped" aria-describedby="Cache">
        <thead>
          <tr>
            <th scope="col">Nome da cache</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.hits">Cache Hits</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.misses">Cache Misses</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.gets">Cache Gets</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.puts">Cache Puts</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.removals">Cache Removals</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.evictions">Cache Evictions</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.hitPercent">Cache Hit %</th>
            <th scope="col" class="text-right" data-translate="metrics.cache.missPercent">Cache Miss %</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(entry, key) of metrics.cache" :key="key">
            <td>{{ key }}</td>
            <td class="text-right">{{ entry['cache.gets.hit'] }}</td>
            <td class="text-right">{{ entry['cache.gets.miss'] }}</td>
            <td class="text-right">{{ entry['cache.gets.hit'] + entry['cache.gets.miss'] }}</td>
            <td class="text-right">{{ entry['cache.puts'] }}</td>
            <td class="text-right">{{ entry['cache.removals'] }}</td>
            <td class="text-right">{{ entry['cache.evictions'] }}</td>
            <td class="text-right">
              {{ formatNumber2(filterNaN((100 * entry['cache.gets.hit']) / (entry['cache.gets.hit'] + entry['cache.gets.miss']))) }}
            </td>
            <td class="text-right">
              {{ formatNumber2(filterNaN((100 * entry['cache.gets.miss']) / (entry['cache.gets.hit'] + entry['cache.gets.miss']))) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <h3>Estatísticas do datasource (tempo em milissegundo)</h3>
    <div class="table-responsive" v-if="!updatingMetrics && isObjectExistingAndNotEmpty(metrics, 'databases')">
      <table class="table table-striped" aria-describedby="Connection pool">
        <thead>
          <tr>
            <th scope="col">
              <span>Utilização</span> (active: {{ metrics.databases.active.value }}, min: {{ metrics.databases.min.value }}, max:
              {{ metrics.databases.max.value }}, idle: {{ metrics.databases.idle.value }})
            </th>
            <th scope="col" class="text-right">Contagem</th>
            <th scope="col" class="text-right">Mediana</th>
            <th scope="col" class="text-right">Min</th>
            <th scope="col" class="text-right">p50</th>
            <th scope="col" class="text-right">p75</th>
            <th scope="col" class="text-right">p95</th>
            <th scope="col" class="text-right">p99</th>
            <th scope="col" class="text-right">Max</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Acquire</td>
            <td class="text-right">{{ metrics.databases.acquire.count }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.acquire.mean)) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.0']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.5']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.75']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.95']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.acquire['0.99']) }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.acquire.max)) }}</td>
          </tr>
          <tr>
            <td>Creation</td>
            <td class="text-right">{{ metrics.databases.creation.count }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.creation.mean)) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.0']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.5']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.75']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.95']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.creation['0.99']) }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.creation.max)) }}</td>
          </tr>
          <tr>
            <td>Usage</td>
            <td class="text-right">{{ metrics.databases.usage.count }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.usage.mean)) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.0']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.5']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.75']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.95']) }}</td>
            <td class="text-right">{{ formatNumber2(metrics.databases.usage['0.99']) }}</td>
            <td class="text-right">{{ formatNumber2(filterNaN(metrics.databases.usage.max)) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <b-modal ref="metricsModal" size="lg">
      <template #modal-title>
        <h4 class="modal-title" id="showMetricsLabel">Threads dump</h4>
      </template>
      <metrics-modal :thread-dump="threadData"></metrics-modal>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./metrics.component.ts"></script>
