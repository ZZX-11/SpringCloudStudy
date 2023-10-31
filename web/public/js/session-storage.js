// 所有的session key都在这里统一定义，可以避免多个功能使用同一个key
SESSION_ORDER = "SESSION_ORDER";
SESSION_TICKET_PARAMS = "SESSION_TICKET_PARAMS";

SessionStorage = {
    get: function (key) {
        var v = sessionStorage.getItem(key);
        if (v && typeof(v) !== "undefined" && v !== "undefined") {
            return JSON.parse(v);
        }
    },
    set: function (key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
    },
    remove: function (key) {
        sessionStorage.removeItem(key);
    },
    clearAll: function () {
        sessionStorage.clear();
    }
};

//这段代码定义了一个名为 SessionStorage 的对象，它封装了对浏览器会话存储（session storage）的操作方法。
// 会话存储是一种在浏览器中存储数据的机制，可以在用户会话期间（即在用户关闭标签页之前）持久保存数据。

// 这个 SessionStorage 对象包含以下方法：

// get(key): 根据指定的键名（key）从会话存储中获取对应的值。如果值存在且不为 undefined 或 "undefined"，则将其解析为 JSON 对象并返回。

// set(key, data): 将指定的键名（key）和数据（data）以 JSON 字符串的形式存储到会话存储中。

// 这段代码还定义了两个常量 SESSION_ORDER 和 SESSION_TICKET_PARAMS，用于统一定义会话存储中的键名，以避免多个功能使用相同的键名。

// 通过使用这个 SessionStorage 对象，你可以方便地在浏览器会话期间存储和获取数据，避免了直接操作 sessionStorage 对象的复杂性，
// 并且通过统一定义键名，可以避免键名冲突的问题。