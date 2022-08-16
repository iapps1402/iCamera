package ir.stackcode.icamera.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import ir.stackcode.icamera.R
import ir.stackcode.icamera.databinding.FragmentContactBinding
import ir.stackcode.icamera.view.MainActivity

class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContactBinding.inflate(layoutInflater)

        binding.compose.setContent {
            var text by remember { mutableStateOf("") }
            Row {
                Box(
                    modifier = Modifier
                        .weight(.5f),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    ) {
                        Row(
                            Modifier.padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "1"
                                }) {
                                Text(
                                    text = "1",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )

                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "2"
                                }) {
                                Text(
                                    text = "2",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "3"
                                }) {
                                Text(
                                    text = "3",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Row(
                            Modifier.padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "4"
                                }) {
                                Text(
                                    text = "4",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "5"
                                }) {
                                Text(
                                    text = "5",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "6"
                                }) {
                                Text(
                                    text = "6",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }


                        Row(
                            Modifier.padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "7"
                                }) {
                                Text(
                                    text = "7",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "8"
                                }) {
                                Text(
                                    text = "8",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "9"
                                }) {
                                Text(
                                    text = "9",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Row(
                            Modifier.padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "*"
                                }) {
                                Text(
                                    text = "*",
                                    fontSize = 25.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "0"
                                }) {
                                Text(
                                    text = "0",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = 10.dp, end = 10.dp))

                            OutlinedButton(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                border = BorderStroke(1.dp, Color.White),
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Transparent,
                                    backgroundColor = Color.Transparent
                                ),
                                onClick = {
                                    text += "#"
                                }) {
                                Text(
                                    text = "#",
                                    fontSize = 25.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(.5f)
                ) {
                    Surface(
                        elevation = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(color = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(15.dp, 10.dp, 15.dp, 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Switch(checked = true, onCheckedChange = {

                                })

                                Text(
                                    text = stringResource(id = R.string.intercom_is_enabled),
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }

                            Divider(
                                color = colorResource(R.color.color_gren), thickness = 1.dp,
                                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 10.dp)
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = stringResource(id = R.string.phone_number),
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(color = colorResource(id = R.color.color_gren2)),
                                    text = text,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }

                            Divider(
                                color = colorResource(R.color.color_gren), thickness = 1.dp,
                                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 10.dp)
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = stringResource(id = R.string.internal_address),
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(color = colorResource(id = R.color.color_gren2)),
                                    text = "008",
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }


                            Divider(
                                color = colorResource(R.color.color_gren), thickness = 1.dp,
                                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 10.dp)
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Switch(checked = true, onCheckedChange = {

                                    })

                                    Text(
                                        text = stringResource(id = R.string.emergency_phone_number),
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                }

                                Divider(
                                    color = colorResource(R.color.color_gren), thickness = 1.dp,
                                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 10.dp)
                                )

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(color = colorResource(id = R.color.color_gren2)),
                                    text = "128",
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }

                        }
                    }
                }

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exitLayout.setOnClickListener {
            (activity as MainActivity).openHome()
        }
    }
}