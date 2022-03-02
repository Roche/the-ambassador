{{/*
Returns the contents of the `ambassador.database.yml` blob for pods
*/}}
{{- define "ambassador.database.url" -}}
{{- if .Values.global.postgresql.host }}
{{ .Values.global.postgresql.host }}:{{ .Values.global.postgresql.port }}
{{- else }}
{{ .Release.Name }}-postgresql.{{ .Release.Namespace }}.svc.cluster.local:{{ .Values.global.postgresql.port }}
{{- end }}
{{- end }}

{{- define "postgres.password" -}}
{{- if .Values.global.postgresql.existingSecret }}
{{- index (lookup "v1" "Secret" .Release.Namespace .Values.global.postgresql.existingSecret).data "postgresql-password" | b64dec }}
{{- else if .Values.global.postgresql.postgresqlPassword }}
{{- .Values.global.postgresql.postgresqlPassword }}
{{- else }}
{{- fail "neither global.postgresql.existingSecret nor global.postgresql.postgresqlPassword are defined" }}
{{- end }}
{{- end }}

{{- define "ambassador.database.yml" -}}
spring:
  datasource:
    url: jdbc:postgresql://{{ include "ambassador.database.url" . | trim }}/{{ .Values.global.postgresql.postgresqlDatabase }}
    username: {{ .Values.global.postgresql.postgresqlUsername }}
    password: {{ include "postgres.password" . }}
{{- end -}}
