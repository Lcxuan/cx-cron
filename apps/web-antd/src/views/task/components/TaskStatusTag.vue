<template>
  <a-tag :color="enabled ? 'success' : 'default'">
    {{ enabled ? '启用' : '关闭' }}
  </a-tag>
</template>

<script setup lang="ts">
defineProps<{ enabled: 0 | 1 }>();
</script>
