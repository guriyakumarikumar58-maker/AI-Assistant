package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Polyline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AutonomousKnowledgeGraph
import com.example.ai.AutonomousKnowledgeGraphEngine
import com.example.ai.KnowledgeGraphNode
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.GeminiBlueLight
import com.example.ui.theme.GeminiTextPrimary
import com.example.ui.theme.GeminiTextSecondary
import com.example.ui.theme.GeminiTextTertiary
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.viewmodel.VoiceLockViewModel

/**
 * KnowledgeGraphVisualizerView:
 * Displays the Autonomous Knowledge Graph representing deep relational
 * connections between topics, models, web evidence, and practical actions.
 */
@Composable
fun KnowledgeGraphVisualizerView(
    viewModel: VoiceLockViewModel,
    modifier: Modifier = Modifier
) {
    val activeGraph by viewModel.latestKnowledgeGraph.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    val graphToDisplay: AutonomousKnowledgeGraph = activeGraph ?: remember {
        AutonomousKnowledgeGraphEngine.buildKnowledgeGraph(
            query = "सामान्य तकनीकी व सार्वभौमिक ज्ञान"
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("autonomous_knowledge_graph_view")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPurple, NeonCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountTree,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ऑटोनॉमस नॉलेज ग्राफ",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeminiTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonPurple.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${graphToDisplay.nodes.size} नोड्स • ${graphToDisplay.edges.size} संबंध",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonPurple
                                )
                            }
                        }
                        Text(
                            text = "विषय: ${graphToDisplay.topicTitleHindi}",
                            fontSize = 10.sp,
                            color = GeminiTextSecondary,
                            maxLines = 1
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isExpanded) "संक्षेप ▲" else "ग्राफ देखें ▼",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Carousel of Knowledge Nodes
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(graphToDisplay.nodes) { node ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceElevated)
                            .border(0.8.dp, Color(node.type.badgeColorHex).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(node.type.badgeColorHex))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = node.labelHindi,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(node.type.badgeColorHex)
                                )
                            }
                            Text(
                                text = node.type.labelHindi,
                                fontSize = 8.sp,
                                color = GeminiTextTertiary
                            )
                        }
                    }
                }
            }

            // Expandable Deep Relational Edges & Summary
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "जटिल विषयों के बीच गहरे रिलेशनल संबंध (Deep Edges):",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeminiBlueLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    graphToDisplay.edges.forEach { edge ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSurfaceElevated)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = edge.sourceId,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = edge.relation.symbol,
                                fontSize = 10.sp,
                                color = NeonAmber
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "[${edge.labelHindi}]",
                                fontSize = 9.sp,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = edge.targetId,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(0.8.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "✨ ज्ञान संश्लेषण व वेब ग्राउंडिंग:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = graphToDisplay.deepRelationalSummary,
                                fontSize = 9.sp,
                                lineHeight = 13.sp,
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }
    }
}
