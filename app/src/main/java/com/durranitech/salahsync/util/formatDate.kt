import java.text.SimpleDateFormat
import java.util.Locale


fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(java.util.Date(timestamp))
}