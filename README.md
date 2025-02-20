# FoodOrderingBot


# Telegram Bot for Food Ordering

A custom Telegram bot developed in Java to streamline the food ordering process for our hall-wide F&B business. The bot leverages the Telegram API to receive and process orders, integrates with Google Sheets for real-time order management, and is deployed on Heroku to ensure scalability and high availability during peak periods.

## Features

- **Automated Order Processing:** Utilizes the Telegram API to interact with users and automate food orders.
- **Real-time Data Storage:** Connects to Google Sheets for tracking orders, inventory, and sales data.
- **Scalable Deployment:** Deployed on Heroku, allowing for seamless handling of high order volumes.
- **User-friendly Interaction:** Provides an intuitive interface for customers to place orders directly through Telegram.

## Getting Started

### Prerequisites

- **Java Development Kit (JDK):** Version 8 or higher.
- **Build Tool:** Maven or Gradle (depending on your preference).
- **Telegram Bot Token:** Obtainable via [BotFather](https://t.me/BotFather).
- **Google Sheets API Credentials:** JSON credentials file from the Google Developer Console.
- **Heroku CLI:** For deployment (if deploying on Heroku).

### Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/shivanicc/FoodOrderingBot.git
   cd FoodOrderingBot


2. **Set Up Environment Variables:**
   
   Create a .env file in the project root or configure your environment variables with the following keys:

   ```bash
   TELEGRAM_BOT_TOKEN=your_telegram_bot_token
   GOOGLE_SHEET_ID=your_google_sheet_id
   GOOGLE_SHEETS_CREDENTIALS=path/to/your/google/credentials.json


3. **Build the Project:**
   
   ```bash
   For Maven: mvn clean install
   

4. **Running Locally:**
   Run the bot by executing the generated JAR file:

   ```bash
   java -jar target/FoodOrderingBot.jar

5. **Deployment**

   Deploying on Heroku
 
    1. **Create a Heroku App:**
   
   ```bash
   heroku create your-heroku-app-name
   
 2. **Set Environment Variables on Heroku:**

```bash   
heroku config:set TELEGRAM_BOT_TOKEN=your_telegram_bot_token
heroku config:set GOOGLE_SHEET_ID=your_google_sheet_id
heroku config:set GOOGLE_SHEETS_CREDENTIALS=path/to/your/google/credentials.json

3.**Deploy Your Code:**
git push heroku main

Usage

Once deployed, share the Telegram bot link with customers. They can interact with the bot to place food orders, which are then automatically recorded in the connected Google Sheet for efficient management and tracking.

Contributing

Contributions are welcome! If you have suggestions, improvements, or bug fixes, please open an issue or submit a pull request.

License

This project is licensed under the MIT License. See the LICENSE file for details.

Acknowledgements

Telegram Bot API
Google Sheets API
Heroku

   



