# Cliquim Arquivos — pranchas Win98 v1

**Fechamento:** consulte o [handoff de 13/11/2026](HANDOFF-2026-11-13.md) para o estado mais recente. Os registros da versão 0.2 abaixo são históricos.

Entrega visual de 12/11/2026: **14 PNGs, 28 telas/estados**, organizados abaixo para o desenvolvimento. A prancha 13 vigente é a revisão **v2**. O arquivo anteriormente chamado corrigido ainda continha onboarding ao fundo e foi removido da árvore atual; permanece apenas no histórico Git. Use exclusivamente `boards/13-tema-escuro-teclado-v2.png`.

## Estado da aprovação e do produto

Müller inicialmente aprovou a direção visual da home e pediu as demais telas. Em 12/11/2026, após consultar este conjunto, pediu expressamente que o APK reproduza o detalhamento destas pranchas, com conferência tela por tela. Esta é a direção vigente para implementação. As pranchas são imagens raster de referência, não Figma editável, assets de produção ou screenshots do APK.

A versão de teste **0.2.0** integra parte do visual e foi instalada no Redmi preservando dados, conforme [registro do Work de desenvolvimento](https://github.com/studiocliquim-max/cliquim-arquivos/blob/70144520fbe1786fc2e40e2b08e2bf4a62abd411/docs/INTEGRACAO_UI_02.md). A implementação continua parcial e a validação completa permanece pendente. Veja a [auditoria das 14 pranchas × código e seis capturas reais](AUDITORIA-VISUAL-02.md), com correções e critérios de aceite. Este branch de design não alterou código, compilou ou instalou o aplicativo.

A home aprovada original permanece na conversa de origem; estas 14 pranchas são suas derivações. 

## Arquivos e uso

- `boards/`: imagens PNG originais geradas, preservadas sem reamostragem. Cada arquivo tem duas telas lado a lado.
- `manifest.json`: nomes, dimensões reais, tamanhos e SHA Git dos blobs para conferência.
- `INTEGRACAO.md`: decisões de interface, diferenças conhecidas e limites do protótipo.
- Abrir os PNGs em tamanho original para inspecionar; não converter a tela inteira em fundo do aplicativo.
- Reconstruir os componentes de forma responsiva. Ilustrações, textos e medidas são referências; validar legibilidade, TalkBack e toques na etapa centralizada autorizada.

## Índice

| Prancha | Arquivo | Dimensões |
| --- | --- | --- |
| 01 — Navegação | [PNG](boards/01-navegacao.png) | 1254 × 1254 |
| 02 — Busca | [PNG](boards/02-busca.png) | 1254 × 1254 |
| 03 — Seleção e destino | [PNG](boards/03-selecao-destino.png) | 1254 × 1254 |
| 04 — Criar e renomear | [PNG](boards/04-criar-renomear.png) | 1254 × 1254 |
| 05 — Transferências | [PNG](boards/05-transferencias.png) | 1254 × 1254 |
| 06 — Conflitos e exclusão | [PNG](boards/06-conflitos-exclusao.png) | 1254 × 1254 |
| 07 — Arquivo e propriedades | [PNG](boards/07-previa-propriedades.png) | 1254 × 1254 |
| 08 — Acesso e desconexão | [PNG](boards/08-acesso-desconexao.png) | 1254 × 1254 |
| 09 — Preferências e armazenamento | [PNG](boards/09-preferencias-armazenamento.png) | 1254 × 1254 |
| 10 — Menus e ações | [PNG](boards/10-menus-acoes.png) | 1536 × 1024 |
| 11 — Pasta vazia e acesso removido | [PNG](boards/11-pasta-vazia-acesso-removido.png) | 1254 × 1254 |
| 12 — Espaço insuficiente e substituição | [PNG](boards/12-espaco-substituicao.png) | 1254 × 1254 |
| 13 — Tema escuro e edição com teclado | [PNG](boards/13-tema-escuro-teclado-v2.png) | 1254 × 1254 |
| 14 — Favoritos e ajuda | [PNG](boards/14-favoritos-ajuda.png) | 1254 × 1254 |

## Pré-visualizações

### 01 — Navegação

![01 — Navegação](boards/01-navegacao.png)

### 02 — Busca

![02 — Busca](boards/02-busca.png)

### 03 — Seleção e destino

![03 — Seleção e destino](boards/03-selecao-destino.png)

### 04 — Criar e renomear

![04 — Criar e renomear](boards/04-criar-renomear.png)

### 05 — Transferências

![05 — Transferências](boards/05-transferencias.png)

### 06 — Conflitos e exclusão

![06 — Conflitos e exclusão](boards/06-conflitos-exclusao.png)

### 07 — Arquivo e propriedades

![07 — Arquivo e propriedades](boards/07-previa-propriedades.png)

### 08 — Acesso e desconexão

![08 — Acesso e desconexão](boards/08-acesso-desconexao.png)

### 09 — Preferências e armazenamento

![09 — Preferências e armazenamento](boards/09-preferencias-armazenamento.png)

### 10 — Menus e ações

![10 — Menus e ações](boards/10-menus-acoes.png)

### 11 — Pasta vazia e acesso removido

![11 — Pasta vazia e acesso removido](boards/11-pasta-vazia-acesso-removido.png)

### 12 — Espaço insuficiente e substituição

![12 — Espaço insuficiente e substituição](boards/12-espaco-substituicao.png)

### 13 — Tema escuro e edição com teclado

![13 — Tema escuro e edição com teclado](boards/13-tema-escuro-teclado-v2.png)

### 14 — Favoritos e ajuda

![14 — Favoritos e ajuda](boards/14-favoritos-ajuda.png)
