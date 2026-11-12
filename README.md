# Cliquim Arquivos

Gerenciador local para Android inspirado em interfaces de desktop dos anos 1990, com ativos vetoriais próprios. Protótipo funcional 0.3.0 para avaliação, não publicado na Play Store.

## Implementado
- Navegação por pastas autorizadas via SAF e armazenamento compartilhado com concessão especial do usuário.
- Lista/grade, ordenação, busca por nome e categorias na pasta atual, favoritos, tema claro/escuro.
- Prévia interna de imagens, assíncrona e limitada a 2048 px por eixo, com orientação EXIF, anterior/próxima, propriedades, abrir com e compartilhar.
- Criar pasta, renomear arquivos locais, seleção múltipla, copiar arquivos/pastas com nomes únicos e verificação SHA-256.
- Mover arquivos locais após validação; exclusão explicitamente permanente; abrir/compartilhar pelo Android.
- Registro local de resultado e aviso após interrupção. Não retoma operações incertas automaticamente.
- Sem permissão INTERNET, SDK de anúncios, telemetria ou conta.

## Limitações explícitas
- Busca não é recursiva; filtros por data/tamanho, recentes, miniaturas na listagem e análise de volume ainda não implementados.
- Renomear via SAF, mover pasta, pastas entre os modos local/SAF, substituição e lixeira não disponíveis.
- Mover arquivo SAF conserva a origem; relata cópia confirmada e original mantido.
- Operações em Activity, sem serviço: mantenha aberto durante transferências. Após encerramento, consulte resultado e arquivos. Cópia parcial fica no destino para revisão, sem limpeza destrutiva automática.
- Nenhuma garantia de atomicidade, backup ou proteção absoluta contra alterações concorrentes/falhas físicas.
- SD/USB via seletor do Android sujeito a hardware/provedor; desconexão física ainda requer validação.
- Android/data e Android/obb privados excluídos. Não usa root.
- UI inicial adaptada ao toque; aprovação visual final pendente, não reprodução pixel-perfect.

## Compilar
JDK 17 e Android SDK 36:
```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
```
APK em `app/build/outputs/apk/debug/app-debug.apk`.
Assinatura debug apenas para teste. Não armazenar chaves no repositório.

## Referência
https://guidebookgallery.org/screenshots/win98se/
Imagem Explorer 98 SE inspecionada visualmente; imagens históricas não incorporadas ao APK. Cores inspiradas, ícones vetoriais originais. Sem logotipos/sons/fontes Microsoft. Revisão jurídica e de marca antes da venda.

## Interface 0.3 (aguardando testes centralizados)
Relevo duplo, ícones próprios detalhados, colunas na lista, rodapé de seleção, menus planos, endereço separado e diálogos pelo conteúdo. Favoritos, preferências e propriedades têm dados reais; a prévia de imagens foi acrescentada. Não é a reprodução final das 14 pranchas. [Entrega e pendências da 0.3](docs/ENTREGA_03.md). [Histórico da 0.2](docs/INTEGRACAO_UI_02.md).
