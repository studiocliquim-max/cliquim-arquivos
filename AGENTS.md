# Regras de entrega

## Entrega completa da família 98

- Preserve o APK independente deste projeto, seu package ID e os dados nas atualizações.
- Para uma entrega completa, atualize o commit deste projeto em `suite98/sources.json` e execute `python3 tools/build_all.py` no repositório Suíte 98. O resultado deve incluir oito APKs individuais, um APK integrado e o manifesto de hashes. Uma compilação isolada serve ao desenvolvimento, mas não substitui essa entrega completa.
- Não publique uma build de desenvolvimento como pronta para a Play Store. Mantenha assinatura, evidências e limitações explícitas.
- Testes de dispositivos são sequenciais; não altere áudio, espelhamento ou dados do telefone sem escopo autorizado.
