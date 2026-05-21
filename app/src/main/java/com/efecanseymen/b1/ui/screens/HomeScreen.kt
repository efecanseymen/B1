package com.efecanseymen.b1.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.efecanseymen.b1.viewmodel.HomeViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    viewModel: HomeViewModel, modifier: Modifier, onLogOutClick: () -> Unit
) {
    var userName = viewModel.currentUserName ?: "Öğrenci"


    val list1 = listOf<ClassInfo>(ClassInfo("BLM1001","%74 devamlılık"),ClassInfo("BLM2545","%0 devamlılık"),ClassInfo("BLM3344","%100 devamlılık"))



    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        TopAppBar(
            title = {
                Row {
                    Text(
                        text = ("Hoşgeldin " + userName.substringBefore(' ') + "!"),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 5.dp)




                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { onLogOutClick() },
                        modifier = Modifier
                            .padding(end = 15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )

                    ) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = null
                        )
                    }
                } },
            modifier = Modifier
                .height(85.dp)
                .fillMaxWidth()


        )
        Text(
            text = "Geçmiş Derslerin "+"("+list1.size.toString()+")",
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier.size(12.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { items(list1) { item ->
            ListItem(
                modifier, item
                )
                }
        }
    }
}

data class ClassInfo(
    val className: String,
    val description: String
)

@Composable
fun ListItem(
    modifier: Modifier,
    classInfo: ClassInfo
){
    val className = classInfo.className
    val description = classInfo.description
    Surface() {
        Row(modifier = Modifier.fillMaxWidth()) {

            // Renkli şerit
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        color = if (classInfo.description == "başarılı")
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFCF6679),
                        shape = RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)
                    )
            )
        Column (
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clickable {},
            ){
            Text(
                text = className,
                fontWeight = Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()

                )
            Spacer(
                modifier = Modifier.size(5.dp)
            )
            Text(

                text = "Son Yoklama Durumu: " + description,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )

        }
    }
}
}


