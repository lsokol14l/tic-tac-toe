const {createApp, ref, computed, onMounted} = Vue

createApp({
    setup() {
        const username = ref('')
        const currentUserId = ref(null)
        const authToken = ref(null)
        const myGames = ref([])
        const loading = ref(false)
        const error = ref('')

        const totalGames = computed(() => myGames.value.length)
        const wins = computed(() => myGames.value.filter(g => g.status === 'PLAYER_WIN' && g.winnerId === currentUserId.value).length)
        const losses = computed(() => myGames.value.filter(g => g.status === 'PLAYER_WIN' && g.winnerId !== currentUserId.value).length)
        const draws = computed(() => myGames.value.filter(g => g.status === 'DRAW').length)

        const gameResultForMe = game => {
            if (game.status === 'PLAYING' || game.status === 'WAITING_FOR_PLAYERS') return 'В процессе'
            if (game.status === 'DRAW') return 'Ничья'
            if (game.status === 'PLAYER_WIN') return game.winnerId === currentUserId.value ? 'Победа' : 'Поражение'
            return '—'
        }

        const gameModeLabel = mode => {
            if (mode === 'AI') return 'Против бота'
            if (mode === 'MULTIPLAYER') return 'Онлайн'
            if (mode === 'LOCAL') return 'Локально'
            return mode || '—'
        }

        const loadMyGames = async () => {
            loading.value = true
            error.value = ''
            try {
                const res = await fetch('/game/my', {
                    headers: {Authorization: 'Basic ' + authToken.value}
                })
                if (res.status === 401) {
                    window.location.href = '/'
                    return
                }
                if (!res.ok) throw new Error('Ошибка загрузки')
                myGames.value = await res.json()
            } catch (e) {
                error.value = e.message
            } finally {
                loading.value = false
            }
        }

        const goBack = () => {
            window.location.href = '/'
        }

        const goToGame = gameId => {
            localStorage.setItem('resumeGameId', gameId)
            window.location.href = '/'
        }

        onMounted(() => {
            const token = localStorage.getItem('authToken')
            const savedUsername = localStorage.getItem('username')
            const savedUserId = localStorage.getItem('userId')

            if (!token || !savedUsername || !savedUserId) {
                window.location.href = '/'
                return
            }

            authToken.value = token
            username.value = savedUsername
            currentUserId.value = savedUserId

            loadMyGames()
        })

        return {
            username,
            currentUserId,
            myGames,
            loading,
            error,
            totalGames,
            wins,
            losses,
            draws,
            gameResultForMe,
            gameModeLabel,
            loadMyGames,
            goBack,
            goToGame
        }
    }
}).mount('#app')