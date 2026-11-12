# Integração visual 0.2.0 • 12/11/2026

## Escopo
Direção aprovada da home: cinza clássico, título azul em degradê, área de arquivos branca, menus Arquivo/Editar/Exibir/Favoritos, ferramentas, endereço, bordas em relevo. Ícones desenhados no código, sem distribuir recursos Microsoft.
As outras telas do branch design/pranchas-win98-2026-11-12 permanecem em revisão; prancha 13 foi substituída pela v2 no commit 3f300960. Não se considera o conjunto aprovado nem pixel-perfect.

## Entregue
- Home com entradas reais para armazenamento autorizado e seletor de pasta; controles incompatíveis desativados.
- Lista/grade com seleção visível, ícones por tipo, menus e diálogos próprios. Permissões, seletor e compartilhamento continuam nativos Android.
- Endereço real, busca explicitamente na pasta atual, status e ações com alvos de toque maiores que o desktop original.
- Validação inline de nomes mantém diálogo aberto. Renomeação preserva extensão em campo separado.
- Limpeza da lista anterior durante carregamento para evitar ações sobre entradas antigas.
- Tema escuro com ajuste de contraste nas setas. Nenhuma mudança no FileEngine.

## Não implementado / não simulado
Busca recursiva, filtros de tamanho/data, recentes, análise de volume, thumbnails internos, fila persistente com progresso de bytes, substituição, lixeira, renomeação SAF e movimento de pastas. O README conserva os limites e riscos reais do motor.

## Evidência
- Build debug, APK de testes e lint final concluídos, 0 erros lint.
- Antes dos dois ajustes finais cosméticos: 21 verificações instrumentadas do motor passaram; 16 verificações UI passaram (home, estados habilitados, arquivos reais da fixture, seleção, voltar, grade, busca, tema e conservação da pasta ao abrir diálogos).
- Fixtures exclusivas no emulador: Cliquim-QA-1794503285013 e Cliquim-UI-QA-1794503319846.
- Capturas em screenshots/v0.2 são do teste anterior ao ajuste final de contraste das setas e redução de altura do diálogo; não são mockups. Capturas home, menu, dark e new-folder inspecionadas.
- Emulador encerrou antes da reinstalação do build final; a tentativa retornou device not found. Portanto os últimos dois ajustes foram compilados/lintados mas não reexecutados em dispositivo.
- Redmi não foi usado nesta etapa para não interromper interação do usuário. Versão 0.2 ainda não instalada nele; 0.1 havia sido instalada preservando dados.

## Próxima janela de QA
Reexecutar UiSmokeTest no build final; teclado aberto/fontes grandes/TalkBack, layouts pequenos e rotação; teste visual Redmi quando liberado; fluxos SAF/provedores reais, USB, grandes transferências e morte do processo antes de produção. Dialog Nova pasta ainda precisa refinamento responsivo com teclado; não declarado final.

## Comandos
```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug
# runner UI separado
./gradlew :app:assembleDebugAndroidTest -PtestRunner=com.studiocliquim.arquivos.UiSmokeTest
adb -s <emulador> install -r app/build/outputs/apk/debug/app-debug.apk
adb -s <emulador> install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s <emulador> shell am instrument -w com.studiocliquim.arquivos.test/com.studiocliquim.arquivos.UiSmokeTest
```
APK final debug SHA256: 2a5318c4409f7ac50b92bf2644c754b7e6678fa672bd1cf21c241da9ccb4a79e.

## Atualização: janela física Redmi
0.2.0 (versionCode 2) atualizada via `adb install -r` com resultado Success, preservando dados; versão confirmada pelo PackageManager. Tela de listagem aberta e inspecionada sem cortes/ANR. Permissões já existentes foram preservadas, sem novas concessões. Captura física não enviada ao repositório porque contém nomes de pastas reais.
Tentativa de executar UiSmokeTest no Redmi interrompida: Gravador assumiu o primeiro plano durante a execução. Encerrados somente processos Arquivos e seu teste, para não interferir no outro aplicativo. O retorno Process crashed decorreu dessa interrupção deliberada e não comprova crash espontâneo. Os 16 checks finais no build final continuam pendentes. Não alterados arquivos pessoais nem launcher padrão. Aparelho liberado após inspeção.
21 testes puros de nomes/conflitos repetidos no host e aprovados nesta etapa. Repositório privado, nome, descrição, PR e remoto conferidos.
