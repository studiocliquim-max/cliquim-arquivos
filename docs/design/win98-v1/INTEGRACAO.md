# Integração visual — pontos obrigatórios

## Como interpretar as pranchas

A aparência aprovada usa cinza clássico, barra de título azul, menus em português, bordas finas em relevo, área de arquivos branca, ícones pequenos e seleção azul com foco pontilhado. O modelo é Explorer/Win98 adaptado ao toque, com marca Cliquim.

As 14 imagens são propostas raster geradas por IA, com dados demonstrativos. Não são um kit de componentes nem uma especificação pixel-perfect. Manter a direção aprovada e consolidar um único sistema de componentes antes de integrar. Não incorporar os bitmaps como telas interativas.

## Tokens e componentes propostos

- Superfície clara #C0C0C0; conteúdo branco; sombra #808080; contorno escuro; destaque branco.
- Título navy #000080 com transição discreta para azul; seleção navy com texto branco.
- Texto principal regular e escalável; usar fonte de sistema ou licenciada, sem redistribuir fontes Microsoft.
- Tema escuro com superfícies carvão e texto claro; medir contraste real, não copiar cada nuance da imagem.
- Componentes compartilhados: título, menu, toolbar, endereço, lista/grade, status, diálogo, botão, checkbox, seletor e menu contextual.
- Ícones autorais consistentes em grade pequena. Alguns conceitos gerados lembram ícones de terceiros, inclusive PDF: substituir por ícones próprios; não extrair logos dos PNGs.
- Mínimo 48 dp para área de toque; conteúdo visual pode ser compacto. Não copiar texto pequeno dos PNGs sem ajuste de escala.
- Android mantém teclado, seletor de arquivos, permissões e compartilhamento nativos. Não imitar permissões do sistema.

## Correções conhecidas a consolidar

1. Os tamanhos/nomes/datas dos arquivos variam entre pranchas; são exemplos e devem vir do estado real do aplicativo.
2. A barra de endereço deve mostrar a localização real. Em alguns conceitos aparece Meu dispositivo apesar de a pasta visível ser Trabalho.
3. Há inconsistências nos botões X de diálogos, em alguns ícones e barras de progresso. Padronizar; fechar diálogo equivale a cancelar.
4. Desabilitar ou remover ações sem contexto: Nova pasta em ajuda/permissões, seleção sem arquivos, Próxima no último item etc. Toolbars não devem aparentar operar durante um modal.
5. A linha de status da prévia deve refletir o índice real e desabilitar navegação nos limites.
6. Botões de favoritos removem apenas o atalho. Não excluir o arquivo de origem.
7. Usar exclusivamente `boards/13-tema-escuro-teclado-v2.png`. A exportação anteriormente chamada corrigida estava incorreta e foi retirada da árvore atual. A v2 foi inspecionada: mostra `Interno > Trabalho`, lista de pastas/arquivos autorizada e o diálogo Nova pasta acima do teclado, sem onboarding. O SHA Git vigente está no manifesto; nenhuma alteração foi feita no APK.
8. Miniaturas e prévias precisam preservar proporções. Não presumir datas, tamanho, proprietário ou metadados ausentes.
9. Menus devem caber e rolar com fonte grande, e ter foco acessível. Arrastar e toque longo não podem ser o único modo de acionar funções.
10. Datas e capacidades ilustradas não constituem leitura do celular do usuário.

## Diferenças para o protótipo 0.1.0

Conferido no README e em docs/QA.md no commit e095a273f67589e8090269943b3b6ac73121df3d:

- Busca atual é por nome na pasta, sem recursão ou filtros por data/tamanho. A prancha 02 representa a direção completa, não função já entregue.
- Prévia/miniaturas internas e análise de volume ainda não implementadas (pranchas 07 e 09).
- Substituição de arquivos não disponível; manter desabilitada até suportar verificação e preservação do destino antigo (06 e 12).
- Renomear via SAF e mover pastas não disponíveis; movimentação SAF conserva o original. UI deve refletir a capacidade real.
- Transferências atuais executam na Activity, sem serviço. Não prometer continuidade em segundo plano, pausa ou retomada segura. Resultados incertos exigem revisão.
- Cópias falhas podem deixar parciais no destino. Não mostrar remoção de parciais como concluída sem evidência.
- SD/USB real, desconexão, acessibilidade e transferências grandes ainda precisam de validação específica.
- Instalação no Redmi registrada pelo outro Work; esta entrega não instalou nem testou o APK.

## Sequência sugerida

1. Revisar pranchas com Müller e consolidar a home como referência comum.
2. Integrar componentes e navegação já suportados, preservando o motor de arquivos e suas proteções.
3. Expor somente ações realmente disponíveis. Planejar funções novas separadamente.
4. Capturar telas do emulador e Redmi, comparar em escala real; testar fontes grandes, TalkBack, orientação, teclado e modal.
5. Validar operações com fixtures isoladas antes de usar arquivos pessoais.
6. Não publicar na Play Store nem apresentar o protótipo como final com base apenas nestas imagens.
