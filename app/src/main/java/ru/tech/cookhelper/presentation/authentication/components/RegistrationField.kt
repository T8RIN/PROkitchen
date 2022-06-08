package ru.tech.cookhelper.presentation.authentication.components

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ru.tech.cookhelper.R
import ru.tech.cookhelper.presentation.authentication.viewModel.AuthViewModel

@Composable
fun RegistrationField(mod: Float, viewModel: AuthViewModel) {

    var nick by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordRepeat by rememberSaveable { mutableStateOf("") }

    var isPasswordVisible by remember {
        mutableStateOf(false)
    }
    val isFormValid by derivedStateOf {
        nick.isNotEmpty() && password.isNotEmpty() && email.isValid() && passwordRepeat == password
    }

    val focusManager = LocalFocusManager.current


    Text(stringResource(R.string.register), style = MaterialTheme.typography.headlineLarge)
    Spacer(Modifier.size(8.dp * mod))
    Text(
        stringResource(R.string.create_your_new_account),
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(Modifier.size(32.dp * mod))
    OutlinedTextField(
        value = nick,
        onValueChange = { nick = it },
        label = { Text(stringResource(R.string.nick)) },
        singleLine = true,
        isError = nick.isEmpty(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.width(TextFieldDefaults.MinWidth),
        trailingIcon = {
            if (nick.isNotBlank())
                IconButton(onClick = { nick = "" }) {
                    Icon(Icons.Filled.Clear, null)
                }
        }
    )
    Spacer(Modifier.size(8.dp * mod))
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text(stringResource(R.string.email)) },
        singleLine = true,
        isError = email.isEmpty() || email.isNotValid(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.width(TextFieldDefaults.MinWidth),
        trailingIcon = {
            if (email.isNotBlank())
                IconButton(onClick = { email = "" }) {
                    Icon(Icons.Filled.Clear, null)
                }
        }
    )
    Spacer(Modifier.size(8.dp * mod))
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(stringResource(R.string.password)) },
        singleLine = true,
        isError = password.isEmpty() || passwordRepeat != password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),
        modifier = Modifier.width(TextFieldDefaults.MinWidth),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                isPasswordVisible = !isPasswordVisible
            }) {
                Icon(
                    if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    null
                )
            }
        }
    )
    Spacer(Modifier.size(8.dp * mod))
    OutlinedTextField(
        value = passwordRepeat,
        onValueChange = { passwordRepeat = it },
        label = { Text(stringResource(R.string.repeat_password)) },
        singleLine = true,
        isError = passwordRepeat.isEmpty() || passwordRepeat != password,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        modifier = Modifier.width(TextFieldDefaults.MinWidth),
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            if (isFormValid) viewModel.registerWith(nick, email, password)
        }),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = {
                isPasswordVisible = !isPasswordVisible
            }) {
                Icon(
                    if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    null
                )
            }
        }
    )

    Spacer(Modifier.size(32.dp * mod))
    Button(
        enabled = isFormValid,
        onClick = { viewModel.registerWith(nick, email, password) },
        modifier = Modifier.defaultMinSize(
            minWidth = TextFieldDefaults.MinWidth
        ), content = { Text(stringResource(R.string.sign_up)) }
    )
    Spacer(Modifier.size(64.dp * mod))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.have_account),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(Modifier.size(12.dp))
        TextButton(
            onClick = { viewModel.openLogin() },
            content = { Text(stringResource(R.string.log_in_have_acc)) })
    }
    Spacer(Modifier.size(8.dp * mod))
}

fun String.isValid(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isNotValid() = !isValid()