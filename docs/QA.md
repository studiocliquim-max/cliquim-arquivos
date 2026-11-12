# Verificação 2026-11-12

## Passou
- Gradle 8.13 / JDK 17 / SDK 36: assembleDebug e assembleDebugAndroidTest.
- Android lint: 0 erros; avisos de strings/localização, API alvo mais antiga que preview 37, backup, caminhos protegidos e gravação síncrona do pequeno journal.
- 21 verificações puras de nomes e conflitos em tests/FileEngineTest.java.
- 21 verificações instrumentadas no emulador Android: criação/listagem, cópia e bytes, conflito sem sobrescrever, renomeação e conflito, movimento local validado, proteção contra subpasta, cópia recursiva, cancelamento, exclusão somente de fixture, rejeição de caminhos privados/traversal, Activity e menu.
- Fixture isolada no emulador: Download/Cliquim-QA-1794500845001. Não usa arquivos pessoais.
- APK 0.1.0 (versionCode 1) instalado via adb install -r no Redmi 2209116AG conectado. Nenhuma permissão ampla concedida automaticamente no aparelho físico.

## Pendente / não afirmar conclusão
- Inspeção visual limpa do APK: captura do emulador foi sobreposta por ANR do outro app Retrozim MVP; no Redmi outro fluxo abriu escolha do launcher padrão. Não alteramos nem fechamos esses fluxos.
- UI final está sendo refinada no Work original. Este APK é um protótipo funcional, não reprodução final aprovada.
- Fluxos SAF reais, remoção de USB, grandes transferências/process death, TalkBack, fontes grandes, versões Android 26–35 e diferentes fabricantes ainda precisam de testes específicos.
- Compartilhamento para aplicativos externos, análise de acesso/provedor em cenários adversos e concorrência precisam de revisão adicional.
- Instalação no Redmi confirmada; Activity criada, mas interrompida por outro fluxo. Não declarar teste completo no aparelho.

## Segurança de operação
Sem sobrescrever. Cópias falhas conservam destinos parciais com aviso. Move de pastas indisponível; move SAF conserva original. Nenhuma operação é repetida automaticamente após encerramento. Exclusão permanente requer diálogo explícito. Não há garantia contra corrupção física ou alterações concorrentes.

## Atualização 0.2
Inspeção visual parcial no emulador e 16 checks de UI concluídos sem sobreposição. Últimos dois ajustes cosméticos somente build/lint, pois o emulador foi encerrado. Ver INTEGRACAO_UI_02.md para distinção exata, screenshots e pendências. Inspeção física Redmi continua pendente.

## Redmi 0.2 confirmado
Atualização preservando dados concluída e versão 2/0.2.0 verificada. Listagem inicial inspecionada visualmente sem cortes/ANR. Instrumentação final abortada deliberadamente ao detectar Gravador em primeiro plano, sem concluir os 16 checks; não interpretar Process crashed da interrupção como crash espontâneo. Não compartilhada captura contendo nomes reais de pastas. Ver registro de integração.

## Atualização 0.3 — testes centralizados pendentes
Build debug e APK de instrumentação compilados; lint: 0 erros, 23 avisos. Passaram 21 checks de nomes/conflitos e 9 checks de limite de resolução de imagens, todos no JVM local. Não são testes de I/O em Android.
Nenhum simulador ou celular utilizado nesta retomada. Por direção de Müller, a execução e auditoria serão feitas por um único Work depois que todos os APKs estiverem preparados. Os resultados da 0.1/0.2 acima são históricos e não validam a 0.3.
UiSmokeTest agora inclui prévia real de PNG de fixture, retorno à pasta e proteção contra leitura atrasada após voltar à home. Foi compilado, mas NÃO executado. Veja ENTREGA_03.md.

## Atualização 0.4 — build local; Android ainda não executado
Build debug e instrumentação compilados; lint com 0 erros e 52 avisos. Passaram 60 verificações no JVM: 21 de nomes/conflitos, 9 de limites de imagem, 13 de travessia somente leitura e 17 de filtros/soma de tamanhos.
UiSmokeTest ampliado para páginas, retorno de propriedades à prévia, favoritos de arquivos e conflito com política Pular preservando conteúdo/original; somente compilado. Nenhum teste em simulador ou celular. Capturas, desempenho/RAM e validação nas versões Android pertencem ao Work único posterior. Ver ENTREGA_04.md.
