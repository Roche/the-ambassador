{{/*
Returns the contents of the `database.yml` blob for Rails pods
*/}}
{{- define "ambassador.database.url" -}}
{{- if .Values.global.postgresql.host }}
{{ .Values.global.postgresql.host }}:{{ .Values.global.postgresql.port }}
{{- else }}
ambassador-postgresql.{{ .Release.Namespace }}.svc.cluster.local:{{ .Values.global.postgresql.port }}
{{- end }}
{{- end }}

{{- define "database.yml" -}}
spring:
  datasource:
    url: jdbc:postgresql://{{ include "ambassador.database.url" . | trim }}/{{ .Values.global.postgresql.postgresqlDatabase }}
    username: {{ .Values.global.postgresql.postgresqlUsername }}
    password: {{ .Values.global.postgresql.postgresqlPassword }}
{{- end -}}
