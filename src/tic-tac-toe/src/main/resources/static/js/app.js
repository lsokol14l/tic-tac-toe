const {createApp, ref, computed} = Vue

createApp({
    setup() {
        // ===== АВТОРИЗАЦИЯ =====
        const isAuthenticated = ref(false)
        const isLoginMode = ref(true)
        const username = ref('')
        const password = ref('')
        const authLoading = ref(false)
        const authError = ref('')
        const successMessage = ref('')
        const authToken = ref('')

        // ===== ЛОББИ =====
        const showLobby = ref(false)
        const availableGames = ref([])
        const lobbyLoading = ref(false)

        // ===== ИГРА =====
        const gameId = ref(null)
        const gameMode = ref(null)
        const board = ref([[0, 0, 0], [0, 0, 0], [0, 0, 0]])
        const gameOver = ref(false)
        const gameStatus = ref(null)
        const currentUserId = ref(null)
        const winnerId = ref(null)
        const player1Id = ref(null)
        const player2Id = ref(null)
        const player1Symbol = ref(null)
        const player2Symbol = ref(null)
        const currentTurnPlayerId = ref(null)
        const statusDescription = ref('')
        const error = ref('')
        const loading = ref(false)
        const pollingInterval = ref(null)

        // ===== COMPUTED =====
        const flatBoard = computed(() => {
            if (!board.value) return Array(9).fill(0)
            return board.value.flat()
        })

        const canMove = computed(() => !loading.value && gameStatus.value === 'PLAYING' && currentTurnPlayerId.value === currentUserId.value)

        const statusText = computed(() => {
            if (loading.value) return 'Обработка хода...'
            if (gameStatus.value === 'WAITING_FOR_PLAYERS') return 'Ожидание второго игрока...'

            if (gameStatus.value === 'PLAYING') {
                return currentTurnPlayerId.value === currentUserId.value ? 'Ваш ход' : 'Ход противника...'
            }

            if (gameStatus.value === 'PLAYER_WIN') {
                let winnerSymbol = 'X'
                if (winnerId.value === player1Id.value) {
                    winnerSymbol = player1Symbol.value === 'MAX' ? 'X' : 'O'
                } else if (winnerId.value === player2Id.value) {
                    winnerSymbol = player2Symbol.value === 'MAX' ? 'X' : 'O'
                }
                return `Победил ${winnerSymbol}!`
            }

            if (gameStatus.value === 'DRAW') return 'Ничья!'

            return statusDescription.value || 'Ожидание...'
        })

        const cellSymbol = value => {
            if (value === 1) return 'X'
            if (value === -1) return 'O'
            return ''
        }

        // ===== ВСПОМОГАТЕЛЬНАЯ ФУНКЦИЯ — читает реальное тело ошибки с сервера =====
        const handleResponse = async res => {
            if (res.status === 401) throw new Error('Требуется авторизация')
            if (!res.ok) {
                const text = await res.text()

                try {
                    const json = JSON.parse(text)
                    if (typeof json === 'object' && json !== null) {
                        throw new Error(Object.values(json).join('; '))
                    }
                } catch (e) {
                    if (e instanceof SyntaxError) {
                        throw new Error(text || `HTTP error! status: ${res.status}`)
                    }
                    throw e
                }
            }
            return res.json()
        }

        // ===== ОБЩЕЕ =====
        const applyGameState = data => {
            gameId.value = data.id
            gameMode.value = data.gameMode
            board.value = data.gameFieldDto.gameField
            gameStatus.value = data.status
            player1Id.value = data.player1Id
            player2Id.value = data.player2Id
            player1Symbol.value = data.player1Symbol
            player2Symbol.value = data.player2Symbol
            currentTurnPlayerId.value = data.currentTurnPlayerId
            winnerId.value = data.winnerId
            statusDescription.value = data.statusDescription
            gameOver.value = !(data.status === 'PLAYING' || data.status === 'WAITING_FOR_PLAYERS')
        }

        // ===== АВТОРИЗАЦИЯ =====
        const register = async () => {
            authError.value = ''
            successMessage.value = ''
            authLoading.value = true
            try {
                const res = await fetch('/auth/register', {
                    method: 'POST', headers: {'Content-Type': 'application/json'}, body: JSON.stringify({
                        login: username.value, password: password.value
                    })
                })
                await handleResponse(res)
                successMessage.value = 'Регистрация успешна! Теперь войдите в систему.'
                isLoginMode.value = true
                password.value = ''
            } catch (e) {
                authError.value = 'Пользователь с логином ' + username.value + ' уже сущ ествует!'
            } finally {
                authLoading.value = false
            }
        }

        const login = async () => {
            authError.value = ''
            authLoading.value = true
            try {
                const token = btoa(username.value + ':' + password.value)
                const res = await fetch('/auth/login', {
                    method: 'POST', headers: {Authorization: 'Basic ' + token}
                })
                if (!res.ok) throw new Error('Неверный логин или пароль')
                const data = await res.json()
                authToken.value = token
                currentUserId.value = data.userId
                localStorage.setItem('authToken', token)
                localStorage.setItem('username', username.value)
                localStorage.setItem('userId', data.userId)
                isAuthenticated.value = true
            } catch (e) {
                authError.value = e.message
            } finally {
                authLoading.value = false
            }
        }

        const logout = () => {
            localStorage.removeItem('authToken')
            localStorage.removeItem('username')
            localStorage.removeItem('userId')
            isAuthenticated.value = false
            authToken.value = ''
            currentUserId.value = null
            username.value = ''
            password.value = ''
            authError.value = ''
            successMessage.value = ''
            resetGame()
        }

        const checkAuth = async () => {
            const token = localStorage.getItem('authToken')
            const savedUsername = localStorage.getItem('username')
            const savedUserId = localStorage.getItem('userId')
            if (!token || !savedUsername || !savedUserId) return

            authToken.value = token
            username.value = savedUsername
            currentUserId.value = savedUserId
            isAuthenticated.value = true

            // Продолжить игру, если пришли с профиля
            const resumeGameId = localStorage.getItem('resumeGameId')
            if (resumeGameId) {
                localStorage.removeItem('resumeGameId')
                try {
                    const res = await fetch(`/game/${resumeGameId}`, {
                        headers: {Authorization: 'Basic ' + token}
                    })
                    if (res.ok) {
                        applyGameState(await res.json())
                        // Если игра ещё ждёт второго игрока — запускаем поллинг
                        if (gameStatus.value === 'WAITING_FOR_PLAYERS' || gameStatus.value === 'PLAYING') startPolling()
                    }
                } catch (e) {
                    // Тихо игнорируем — просто откроем главный экран
                }
            }
        }

        // ===== ЛОББИ =====
        const loadAvailableGames = async () => {
            lobbyLoading.value = true
            try {
                const res = await fetch('/game/available', {
                    headers: {Authorization: 'Basic ' + authToken.value}
                })
                availableGames.value = await handleResponse(res)
            } catch (e) {
                error.value = e.message
            } finally {
                lobbyLoading.value = false
            }
        }

        const openLobby = () => {
            showLobby.value = true
            loadAvailableGames()
        }

        const closeLobby = () => {
            showLobby.value = false
            availableGames.value = []
        }

        const startPolling = () => {
            if (pollingInterval.value) return
            pollingInterval.value = setInterval(async () => {
                // Останавливаем если игра завершена или нет активной игры
                if (!gameId.value || gameOver.value) {
                    stopPolling()
                    return
                }
                try {
                    const res = await fetch(`/game/${gameId.value}`, {
                        headers: {Authorization: 'Basic ' + authToken.value}
                    })
                    applyGameState(await handleResponse(res))
                    if (gameOver.value) stopPolling()  // игра закончилась
                } catch (e) {
                    // тихая ошибка
                }
            }, 2000)
        }

        const stopPolling = () => {
            if (pollingInterval.value) {
                clearInterval(pollingInterval.value)
                pollingInterval.value = null
            }
        }

        const joinGame = async gameUuid => {
            loading.value = true
            error.value = ''
            try {
                const res = await fetch(`/game/${gameUuid}/join`, {
                    method: 'POST', headers: {Authorization: 'Basic ' + authToken.value}
                })
                applyGameState(await handleResponse(res))
                showLobby.value = false
                startPolling()
            } catch (e) {
                error.value = e.message
                if (e.message.includes('авторизация')) logout()
            } finally {
                loading.value = false
            }
        }

        // ===== ИГРА =====
        const resetGame = () => {
            stopPolling()
            gameId.value = null
            board.value = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
            gameOver.value = false
            gameStatus.value = null
            winnerId.value = null
            player1Id.value = null
            player2Id.value = null
            player1Symbol.value = null
            player2Symbol.value = null
            currentTurnPlayerId.value = null
            statusDescription.value = ''
            error.value = ''
            loading.value = false
            showLobby.value = false
            availableGames.value = []
        }

        const goBack = () => resetGame()

        const createGameWithMode = async mode => {
            error.value = ''
            loading.value = true
            try {
                const res = await fetch(`/game/new/${mode}`, {
                    method: 'POST', headers: {
                        'Content-Type': 'application/json', Authorization: 'Basic ' + authToken.value
                    }
                })
                applyGameState(await handleResponse(res))
                if (gameMode.value === 'MULTIPLAYER' || gameStatus.value === 'PLAYING') startPolling()
                showLobby.value = false
            } catch (e) {
                error.value = e.message
                if (e.message.includes('авторизация')) logout()
            } finally {
                loading.value = false
            }
        }

        const makeMove = async index => {
            if (!canMove.value) return
            const row = Math.floor(index / 3)
            const col = index % 3
            if (board.value[row][col] !== 0) return

            const mySymbol = currentUserId.value === player1Id.value ? player1Symbol.value === 'MAX' ? 1 : -1 : player2Symbol.value === 'MAX' ? 1 : -1

            const newBoard = board.value.map(r => [...r])
            newBoard[row][col] = mySymbol

            loading.value = true
            error.value = ''
            try {
                const res = await fetch(`/game/${gameId.value}`, {
                    method: 'POST', headers: {
                        'Content-Type': 'application/json', Authorization: 'Basic ' + authToken.value
                    }, body: JSON.stringify({
                        id: gameId.value, gameFieldDto: {gameField: newBoard}, status: 'PLAYING'
                    })
                })
                applyGameState(await handleResponse(res))
            } catch (e) {
                error.value = e.message
                if (e.message.includes('авторизация')) logout()
            } finally {
                loading.value = false
            }
        }

        checkAuth()

        return {
            // Auth
            isAuthenticated,
            isLoginMode,
            username,
            password,
            authLoading,
            authError,
            successMessage,
            register,
            login,
            logout,

            // Lobby
            showLobby,
            availableGames,
            lobbyLoading,
            openLobby,
            closeLobby,
            joinGame,
            loadAvailableGames,

            // Game
            gameId,
            flatBoard,
            gameOver,
            error,
            loading,
            canMove,
            statusText,
            cellSymbol,
            goBack,
            makeMove,
            gameMode,
            createGameWithMode
        }
    }
}).mount('#app')
