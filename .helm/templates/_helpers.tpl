{{- define "app.roleArn" -}}
{{- if eq .Values.werf.env "prod" -}}
arn:aws:iam::585533447476:role/bl-network-prod-service
{{- else -}}
arn:aws:iam::585533447476:role/BackendServiceRole_{{ .Values.werf.env }}
{{- end -}}
{{- end -}}

{{- define "app.envVars" -}}
- name: spring_profiles_active
  value: {{ .Values.werf.env | quote }}
- name: DB_HOST
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbHost
- name: DB_PORT
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbPort
- name: DB_USER
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbUsername
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: {{ .Values.werf.name }}
      key: dbPassword
{{- end -}}
