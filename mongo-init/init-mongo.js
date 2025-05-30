const appDB = process.env.APP_MONGO_DB;
const appUser = process.env.APP_MONGO_USER;
const appPassword = process.env.APP_MONGO_PASSWORD;

if (!appDB || !appUser || !appPassword) {
    print("Error: Application database details (APP_MONGO_DB, APP_MONGO_USER, APP_MONGO_PASSWORD) are not set in environment variables.");
    quit(1);
}

db = db.getSiblingDB(appDB);

const creationResult = db.createUser({
    user: appUser,
    pwd: appPassword,
    roles: [
        { role: "readWrite", db: appDB },
    ]
});

if (creationResult && (creationResult.ok === 1 || creationResult.code === 0 || (typeof creationResult === 'object' && Object.keys(creationResult).length === 0))) {
    print(`Successfully created or ensured user '<span class="math-inline">\{appUser\}' with readWrite access to database '</span>{appDB}'.`);
} else if (creationResult && creationResult.errmsg && creationResult.errmsg.includes("UserAlreadyExists")) {
    print(`User '<span class="math-inline">\{appUser\}' already exists in database '</span>{appDB}'. Skipping creation.`);
} else {
    print(`Error creating user '<span class="math-inline">\{appUser\}' for database '</span>{appDB}'. Response:`);
    printjson(creationResult);
}