package ru.tech.cookhelper.presentation.authentication.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.tech.cookhelper.R
import ru.tech.cookhelper.core.Action
import ru.tech.cookhelper.data.remote.api.auth.User
import ru.tech.cookhelper.domain.use_case.check_code.CheckCodeUseCase
import ru.tech.cookhelper.domain.use_case.login.LoginUseCase
import ru.tech.cookhelper.domain.use_case.registration.RegistrationUseCase
import ru.tech.cookhelper.presentation.authentication.components.AuthState
import ru.tech.cookhelper.presentation.authentication.components.CodeState
import ru.tech.cookhelper.presentation.authentication.components.LoginState
import ru.tech.cookhelper.presentation.authentication.components.RegistrationState
import ru.tech.cookhelper.presentation.ui.utils.UIText
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registrationUseCase: RegistrationUseCase,
    private val checkCodeUseCase: CheckCodeUseCase
) : ViewModel() {

    private var timerJob: Job? = null

    var codeTimeout by mutableStateOf(60)

    var currentEmail = ""
    var currentNick = ""
    var currentToken = ""

    private val _codeState = mutableStateOf(CodeState())
    val codeState: State<CodeState> = _codeState

    private val _loginState = mutableStateOf(LoginState())
    val loginState: State<LoginState> = _loginState

    private val _registrationState = mutableStateOf(RegistrationState())
    val registrationState: State<RegistrationState> = _registrationState

    fun openPasswordRestore() {
        _authState.value = AuthState.RestorePassword
    }

    fun logInWith(login: String, password: String) {
        loginUseCase(login, password).onEach { result ->
            when (result) {
                is Action.Loading -> _loginState.value = LoginState(isLoading = true)
                is Action.Empty -> _loginState.value =
                    LoginState(error = UIText.StringResource(R.string.wrong_password_or_nick))
                is Action.Error -> _loginState.value =
                    LoginState(error = UIText.DynamicString(result.message.toString()))
                is Action.Success -> {
                    result.data?.user?.let {
                        currentEmail = it.email
                        currentNick = it.nickname
                        currentToken = it.token
                        if (!it.verified) openEmailConfirmation()
                    }
                    _loginState.value = LoginState(user = result.data?.user)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun openRegistration() {
        _authState.value = AuthState.Registration

        resetCodeState()
    }

    fun openLogin() {
        _authState.value = AuthState.Login
    }

    private fun resetCodeState() {
        _codeState.value = CodeState()
    }

    fun registerWith(
        name: String,
        surname: String,
        nickname: String,
        email: String,
        password: String
    ) {
        registrationUseCase(name, surname, nickname, email, password).onEach { result ->
            when (result) {
                is Action.Loading -> _registrationState.value = RegistrationState(isLoading = true)
                is Action.Error -> _registrationState.value =
                    RegistrationState(error = UIText.DynamicString(result.message.toString()))
                is Action.Success -> {
                    result.data?.user?.let {
                        currentEmail = it.email
                        currentNick = it.nickname
                        currentToken = it.token
                        if (!it.verified) openEmailConfirmation()
                    }
                    _registrationState.value = RegistrationState(user = result.data?.user)
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    private fun reloadTimer() {
        codeTimeout = 60

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (codeTimeout != 0) {
                delay(1000)
                codeTimeout--
            }
            timerJob?.cancel()
        }
    }

    fun checkCode(code: String) {
        checkCodeUseCase(code, currentToken).onEach { result ->
            when (result) {
                is Action.Loading -> _codeState.value = CodeState(isLoading = true)
                is Action.Error -> _codeState.value = CodeState(error = UIText.DynamicString(result.message.toString()))
                is Action.Success -> {
                    result.data?.user?.let {
                        _codeState.value = CodeState(matched = true)
                    }
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun askForCode() {

        reloadTimer()
    }

    fun restorePasswordBy(email: String) {

        openLogin()
    }

    fun openEmailConfirmation() {
        resetCodeState()
        _authState.value = AuthState.ConfirmEmail
        reloadTimer()
    }

    fun resetState() {
        _codeState.value = _codeState.value.copy(matched = false)
        _registrationState.value = _registrationState.value.copy(error = UIText.DynamicString(""))
        _loginState.value = _loginState.value.copy(error = UIText.DynamicString(""))
    }

    private val _authState: MutableState<AuthState> = mutableStateOf(AuthState.Login)
    val authState: State<AuthState> = _authState

}