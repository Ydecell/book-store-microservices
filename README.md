# 📚 book-store-microservices - Run a Book Store API Fast

[![Download the app](https://img.shields.io/badge/Download-Release%20Page-6DB33F?style=for-the-badge&logo=github)](https://github.com/Ydecell/book-store-microservices/releases)

## 🛒 What this is

Book Store Microservices is a Windows-friendly app package for a book store API. It uses several small services that work together. This setup helps keep each part of the app separate, which makes it easier to run and manage.

You use this project when you want a local copy of the book store system. It is meant for end users who want to download the release and start it on Windows.

## 📦 Download

1. Open the release page: https://github.com/Ydecell/book-store-microservices/releases
2. Find the latest release.
3. Download the Windows file or package attached to that release.
4. Save it to a folder you can find again, such as Downloads or Desktop.

If the release page contains more than one file, pick the file that matches Windows. If you see a ZIP file, download the ZIP file first, then extract it before you run the app.

## 🪟 Windows setup

1. Right-click the downloaded ZIP file.
2. Select Extract All.
3. Choose a folder such as `C:\book-store-microservices`.
4. Open the folder after extraction.
5. Look for the main app file, script, or Docker file included in the release.

If the release includes an `.exe` file, double-click it to start the app.

If the release uses Docker, open Docker Desktop first, then follow the included start file or command file.

## ▶️ Run the app

1. Start the main file from the extracted folder.
2. Wait while the services start.
3. Open your browser.
4. Go to the local address shown by the app, such as `http://localhost:8080`.
5. Use the screen or API page that opens.

If the release includes a start script, run that script first. If it includes multiple service files, start the files in the order listed in the release notes.

## 🧭 What you can expect

This app is built as a book store system with separate services. That usually means:

- A service for books
- A service for orders
- A service for users
- A service for login and access control
- A service that helps route requests
- A service that stores data in MySQL
- A service that tracks app activity

The app uses a secure login flow with JWT and internal service tokens. It also uses circuit breakers, which help one service keep running when another service has a problem.

## 💻 Basic system needs

For a smooth start on Windows, use this setup:

- Windows 10 or Windows 11
- 8 GB RAM or more
- 2 GB free disk space
- A recent browser
- Docker Desktop, if the release uses containers
- Java support, if the release includes `.jar` files

If the release includes a MySQL database file or container, keep the database running before you open the app pages.

## 🔧 First-time run steps

1. Download the latest release.
2. Extract the files.
3. Check the folder for a README, start script, or release notes.
4. Start any required database or support services.
5. Start the main app service.
6. Open the local URL in your browser.
7. Sign in if the app asks for a user name and password.

If the app does not open right away, wait one minute and refresh the page.

## 🗂️ Common files you may see

- `docker-compose.yml` — starts the app with Docker
- `start.bat` — starts the app on Windows
- `.jar` files — Java app files
- `application.yml` — app settings
- `README.md` — setup steps and file notes
- `mysql` files or folders — database data

## 🔐 Login and access

The app may ask for a login before you can use it. This is normal for a book store system with user accounts.

You may see:

- A sign-in page
- An admin page
- A user page
- A token-based session that keeps you signed in

If you close the browser, you may need to sign in again.

## 🧪 If the app does not start

1. Check that the files were fully extracted.
2. Make sure Docker Desktop is running, if needed.
3. Make sure no other app is already using the same port.
4. Restart your computer and try again.
5. Open the included log files and look for the first error line.

If you still cannot start it, download the release again and compare the file size with the release page.

## 📁 Folder view

After extraction, the folder may look like this:

- `config`
- `services`
- `database`
- `logs`
- `start`
- `README.md`

You may not see all of these folders. The exact layout depends on the release package you download.

## 🔄 Updating to a newer version

1. Visit the release page again.
2. Download the newest release.
3. Close the app.
4. Extract the new files to a new folder.
5. Start the app from the new folder.

Keep old and new folders separate so you can roll back if needed.

## 🛠️ Troubleshooting checks

- The browser shows nothing: wait for the services to finish starting
- The page says the site cannot be reached: check the local address and port
- The app closes right away: run it from the start file or command file
- The database fails: make sure MySQL is running
- Login fails: check the user name and password from the release notes

## 📌 Release page

Download or install from the release page here:
https://github.com/Ydecell/book-store-microservices/releases

## 🧩 About the project

This project uses common tools for modern app systems, including:

- Java
- Spring Boot
- Spring Cloud
- MySQL
- Liquibase
- MapStruct
- Resilience4j
- JWT
- Docker

These tools help the app store data, manage logins, and keep the services connected

## 🖥️ Open after install

After the app starts, open your browser and go to the local address shown in the app window or in the release notes. If the app includes a web page, use that page to view the store, sign in, or check service status

## 📄 Files for Windows users

If you are using Windows, focus on these file types:

- `.exe` for direct launch
- `.bat` for a start script
- `.zip` for packaged downloads
- `.yml` for Docker setup
- `.jar` for Java app files

Use the file that matches the release instructions on the download page