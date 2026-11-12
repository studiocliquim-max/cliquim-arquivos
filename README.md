# Cliquim Arquivos

Gerenciador local para Android inspirado em interfaces de desktop dos anos 1990, com ativos vetoriais próprios. Protótipo funcional 0.4.0 para avaliação, não publicado na Play Store.

## Implementado
- Navegação por pastas autorizadas via SAF e armazenamento compartilhado com concessão especial do usuário.
- Lista/grade e ordenação; busca por nome, tipo, data e tamanho com subpastas opcionais, sempre em um local autorizado.
- Home autorizada com categorias, favoritos de arquivos/pastas, preferências, propriedades e ajuda em páginas próprias.
- Capacidade local quando informada pelo volume; análise somente leitura dos arquivos acessíveis por categoria, com cancelamento e indicação de cobertura parcial.
- Prévia interna de imagens, assíncrona e limitada a 2048 px por eixo, com orientação EXIF, anterior/próxima, propriedades, abrir com e compartilhar.
- Criar pasta, renomear arquivos locais, seleção múltipla, copiar arquivos/pastas com nomes únicos e verificação SHA-256.
- Mover arquivos locais após validação; exclusão explicitamente permanente; abrir/compartilhar pelo Android.
- Destino navegável, política manter ambos/pular e histórico do último lote com resultado por item. Não retoma operações incertas automaticamente.
- Sem permissão INTERNET, SDK de anúncios, telemetria ou conta.

## Limitações explícitas
- Busca/análise limitadas a 50 mil itens, profundidade 64 e orçamento de 60 s entre chamadas; busca mostra até mil resultados. Uma chamada de provedor pode demorar além disso. Cancelar interrompe entre leituras; não promete interromper um provedor bloqueado.
- Busca não agrega vários locais/volumes em uma única consulta. Sem índice persistente, recentes nem miniaturas na listagem. Metadados ausentes são tratados como desconhecidos.
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

## Interface 0.4 (aguardando testes centralizados)
As 14 pranchas orientam a composição e os estados. Foram implementadas páginas próprias para busca, propriedades, favoritos, preferências, ajuda, armazenamento, destino e transferências, preservando operações reais e desabilitando substituição/retomada não implementadas. A equivalência visual, acessibilidade e desempenho ainda dependem do Work único de testes. [Entrega e limitações da 0.4](docs/ENTREGA_04.md). [Histórico 0.3](docs/ENTREGA_03.md).
