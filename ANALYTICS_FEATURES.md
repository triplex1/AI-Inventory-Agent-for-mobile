# Analytics & Export/Import Features

## Overview
This document describes the new Analytics, Reporting, and Data Import/Export features added to the AI Inventory Agent application.

## Features Added

### 1. Analytics Dashboard
A comprehensive analytics screen that provides:
- **Summary Cards**: Total items, total value, low stock count, out of stock count
- **Stock Level Visualization**: Bar chart showing healthy, low, and out-of-stock items
- **Category Breakdown**: Statistics for each category including item count, total value, and low stock alerts
- **Top Value Items**: List of most valuable inventory items
- **Low Stock Alerts**: Real-time alerts for items that need restocking

### 2. CSV Export
Export your entire inventory to CSV format:
- Includes all item details (name, part number, quantity, price, location, etc.)
- Proper CSV escaping for special characters
- Easy sharing via system share sheet
- Timestamp in filename for version control

**CSV Format:**
```
ID,Name,Part Number,Description,Category,Quantity,Min Quantity,Location,Price,Supplier,Barcode,Created At,Updated At,Tags
```

### 3. CSV Import
Import inventory data from CSV files:
- Bulk import of inventory items
- Format validation before import
- Error handling for invalid rows
- Success/error reporting

**Import Process:**
1. Tap "Import from CSV" button
2. Select CSV file from device
3. System validates format
4. Items are imported to Firestore
5. Success message shows items imported and any errors

### 4. PDF Report Generation
Generate professional PDF reports:
- Summary section with key metrics
- Category breakdown table
- Low stock alerts section
- Top value items section
- Formatted with tables and proper styling
- Shareable via system share sheet

**Report Sections:**
- Title and timestamp
- Summary (total items, value, alerts)
- Category breakdown with values
- Low stock alerts (up to 10 items)
- Top 5 value items

### 5. Low Stock Alert System
Automated monitoring of stock levels:
- Tracks items below minimum quantity
- Color-coded alerts (red for critical)
- Displayed prominently in Analytics screen
- Included in PDF reports

## New Files Created

### Data Models
- `InventoryAnalytics.kt` - Main analytics data class
- `StockLevel.kt` - Stock level breakdown
- `CategoryStats.kt` - Per-category statistics
- `TimeSeriesData.kt` - For future time-based analytics

### Repositories
- `AnalyticsRepository.kt` - Analytics data operations and calculations

### Utilities
- `CsvExporter.kt` - CSV export functionality
- `CsvImporter.kt` - CSV import with validation
- `PdfReportGenerator.kt` - PDF report generation using iText7

### ViewModels
- `AnalyticsViewModel.kt` - Analytics business logic and state management
- `ExportState` - Export operation state management
- `ImportState` - Import operation state management

### UI
- `AnalyticsScreen.kt` - Complete analytics dashboard
- Updated `NavigationItem.kt` - Added Analytics tab
- Updated `MainNavigation.kt` - Added Analytics route

## Dependencies Added

```kotlin
// Charts for Analytics
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

// PDF Generation
implementation("com.itextpdf:itext7-core:7.2.5")

// CSV Parsing
implementation("com.opencsv:opencsv:5.7.1")

// File picker for import
implementation("androidx.activity:activity-compose:1.8.2")
```

## Permissions Added

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="28" />
```

## FileProvider Configuration

Added FileProvider for secure file sharing:

**AndroidManifest.xml:**
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

**file_paths.xml:**
```xml
<paths>
    <cache-path name="shared_files" path="." />
    <external-path name="external_files" path="." />
    <files-path name="internal_files" path="." />
</paths>
```

## Usage

### Accessing Analytics
1. Open the app
2. Tap the "Analytics" tab in the bottom navigation
3. View real-time analytics and statistics

### Exporting Data
1. Navigate to Analytics screen
2. Scroll to "Actions" section
3. Tap "Export to CSV"
4. Choose app to share/save the file

### Generating Reports
1. Navigate to Analytics screen
2. Scroll to "Actions" section
3. Tap "Generate PDF Report"
4. Choose app to share/save the report

### Importing Data
1. Prepare CSV file with correct format
2. Navigate to Analytics screen
3. Tap "Import from CSV"
4. Select your CSV file
5. Wait for import confirmation

## CSV Format Example

```csv
ID,Name,Part Number,Description,Category,Quantity,Min Quantity,Location,Price,Supplier,Barcode,Created At,Updated At,Tags
,Brake Pad Set,BP-001,Premium brake pads,brake,25,5,A-12,45.99,AutoParts Inc,123456789,2024-01-01,2024-01-01,brake;performance
,Oil Filter,OF-001,High-quality oil filter,filter,50,10,B-05,12.99,FilterPro,987654321,2024-01-01,2024-01-01,filter;maintenance
```

**Notes:**
- ID can be empty for new items (auto-generated)
- Tags should be separated by semicolons
- Dates should be in ISO format
- All numeric fields must be valid numbers

## Analytics Calculations

### Stock Levels
- **Healthy**: quantity > (minQuantity * 2)
- **Low Stock**: quantity <= minQuantity but > 0
- **Out of Stock**: quantity == 0

### Category Stats
- **Item Count**: Number of items in category
- **Total Value**: Sum of (quantity * price) for all items
- **Average Price**: Mean price of items in category
- **Low Stock Count**: Items with quantity <= minQuantity

### Top Value Items
Items sorted by (quantity * price) descending, top 5 shown

## Architecture

The features follow MVVM architecture:

```
UI Layer (Composables)
    ?
ViewModel Layer (State Management)
    ?
Repository Layer (Data Operations)
    ?
Data Layer (Firestore, File System)
```

## Error Handling

All operations include proper error handling:
- **Export**: Catches file system errors, shows error message
- **Import**: Validates CSV format, reports invalid rows
- **PDF**: Handles document generation errors
- **Network**: Graceful handling of Firestore errors

## Performance Considerations

- Analytics calculated in real-time using Kotlin Flow
- CSV/PDF operations run on IO dispatcher
- Large imports processed asynchronously
- File operations use efficient buffering

## Future Enhancements

Potential additions:
- [ ] Excel (.xlsx) export support
- [ ] Time-series analytics with charts
- [ ] Scheduled PDF reports
- [ ] Email report delivery
- [ ] Advanced filtering in analytics
- [ ] Custom date range selection
- [ ] Comparative analytics (month-over-month)
- [ ] Inventory forecasting
- [ ] Barcode scanning for quick import

## Testing

To test the features:

1. **Analytics**: 
   - Add various items with different categories
   - Set some items below minimum quantity
   - Check if analytics update in real-time

2. **Export**:
   - Export to CSV and verify data completeness
   - Generate PDF and check formatting

3. **Import**:
   - Create test CSV with valid and invalid data
   - Import and verify error handling
   - Check if items appear in inventory

## Troubleshooting

### "Cannot open file" error
- Check storage permissions are granted
- Ensure the file exists and is accessible

### "Invalid CSV format" error
- Verify CSV has correct header row
- Check for required columns (ID, Name, Part Number, etc.)
- Ensure proper encoding (UTF-8)

### PDF generation fails
- Check available storage space
- Verify iText7 library is properly included
- Check for memory issues with large datasets

### Import shows errors
- Review CSV for invalid data types
- Check numeric fields contain valid numbers
- Verify dates are properly formatted

## Support

For issues or questions:
1. Check this documentation
2. Review error messages carefully
3. Verify CSV format matches template
4. Ensure all permissions are granted

---

**Version**: 1.0.0
**Last Updated**: 2026-01-12
**Author**: AI Inventory Agent Development Team
